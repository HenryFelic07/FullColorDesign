package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fullcolordesign.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditarPerfilUsuarioActivity extends AppCompatActivity implements LocationListener {

    private ImageButton backB, gpsB;

    private ImageView perfilIV;
    private EditText nameET, telefonoET, paisET, provinciaET, ciudadET, direccionET;
    private Button actualizarB;

    // constantes de permiso
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //constantes  foto imagen
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;


    //Arrays permisos
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private double latitud =0.0, longitud =0.0;

    //uri imagen foto
    private Uri image_uri;

    private LocationManager locationManager;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_usuario);

        backB = findViewById(R.id.backB);
        gpsB = findViewById(R.id.gpsB);
        perfilIV = findViewById(R.id.perfilIV);
        nameET = findViewById(R.id.nameET);
        telefonoET = findViewById(R.id.telefonoET);
        paisET = findViewById(R.id.paisET);
        provinciaET = findViewById(R.id.provinciaET);
        ciudadET = findViewById(R.id.ciudadET);
        direccionET = findViewById(R.id.direccionET);
        actualizarB = findViewById(R.id.actualizarB);

        //init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gpsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detecta la localizacion
                if (checkLocationPermission()){
                    //ya permitido
                    detectLocation();
                }
                else {
                    //solicitud no permitida
                    requestLocationPermission();
                }
            }
        });

        actualizarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputData();

            }
        });

        perfilIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //foto usuario
                showImagePickDialog();

            }
        });
    }

    private String name, telefono, pais, provincia, ciudad, direccion;

    private void InputData() {
        //input data
        name = nameET.getText().toString().trim();
        telefono = telefonoET.getText().toString().trim();
        pais = paisET.getText().toString().trim();
        provincia = provinciaET.getText().toString().trim();
        ciudad = ciudadET.getText().toString().trim();
        direccion = direccionET.getText().toString().trim();

        actualizarPerfil();
    }

    private void actualizarPerfil() {
        progressDialog.setMessage("Actualizando Perfil...");

        if (image_uri == null){
            //actualizar sin imagen


            //configuración datos para actualizar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("nombre", ""+ name);
            hashMap.put("telefono", ""+ telefono);
            hashMap.put("pais", ""+ pais);
            hashMap.put("provincia", ""+ provincia);
            hashMap.put("ciudad", ""+ ciudad);
            hashMap.put("direccion", ""+ direccion);
            hashMap.put("latitud", ""+ latitud);
            hashMap.put("longitud", ""+ longitud);

            //actualizar a la base
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //actualizando
                            progressDialog.dismiss();
                            Toast.makeText(EditarPerfilUsuarioActivity.this, "Actualizando Perfil...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //actualizacion fallida
                            progressDialog.dismiss();
                            Toast.makeText(EditarPerfilUsuarioActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }else {
            //actualizar con imagenes
            /*-------Actualizar primero imagen-------*/
            String filePathAndName = "imagenes_perfil" + ""+ firebaseAuth.getUid();
            //obtener referencia almacenamiento
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //iamge uploaded, get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                //recibir la url de la imagen , ahora actualizar en la base
                                //configuración datos para actualizar
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("nombre", ""+ name);
                                hashMap.put("telefono", ""+ telefono);
                                hashMap.put("pais", ""+ pais);
                                hashMap.put("provincia", ""+ provincia);
                                hashMap.put("ciudad", ""+ ciudad);
                                hashMap.put("direccion", ""+ direccion);
                                hashMap.put("latitud", ""+ latitud);
                                hashMap.put("longitud", ""+ longitud);

                                //actualizar a la base
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //actualizando
                                                progressDialog.dismiss();
                                                Toast.makeText(EditarPerfilUsuarioActivity.this, "Actualizando Perfil...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //actualizacion fallida
                                                progressDialog.dismiss();
                                                Toast.makeText(EditarPerfilUsuarioActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditarPerfilUsuarioActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else {
            cargarMiInfo();
        }
    }

    private void cargarMiInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String tipoCuenta = "" + ds.child("tipoCuenta").getValue();
                            String direccion = "" + ds.child("direccion").getValue();
                            String pais = "" + ds.child("pais").getValue();
                            String ciudad = "" + ds.child("ciudad").getValue();
                            String provincia = "" + ds.child("provincia").getValue();
                            String email = "" + ds.child("email").getValue();
                            latitud = Double.parseDouble("" + ds.child("latitud").getValue());
                            longitud = Double.parseDouble("" + ds.child("longitud").getValue());
                            String nombre = "" + ds.child("nombre").getValue();
                            String online = "" + ds.child("online").getValue();
                            String telefono = "" + ds.child("telefono").getValue();
                            String imagenPerfil = "" + ds.child("imagenPerfil").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String uid = "" + ds.child("uid").getValue();

                            nameET.setText(nombre);
                            telefonoET.setText(telefono);
                            paisET.setText(pais);
                            provinciaET.setText(provincia);
                            ciudadET.setText(ciudad);
                            direccionET.setText(direccion);

                            try {
                                Picasso.get().load(imagenPerfil).placeholder(R.drawable.ic_store_gray).into(perfilIV);
                            }catch (Exception e){
                                perfilIV.setImageResource(R.drawable.ic_person_gray);
                            }
                            //nombreTV.setText(name);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showImagePickDialog() {
        //opcion de dialogo pantalla
        String[] option = {"Camara", "Galeria"};
        //dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto Imagen")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //manejador
                        if (which ==0){
                            //camara clic
                            if (checkCamaraPermission()){
                                //permiso de camara permitido
                                fotoCamara();
                            }
                            else {
                                //no permitido solicitud de la camara
                                requestCamaraPermission();
                            }
                        }
                        else{
                            //galeria clic
                            if (checkStoragePermission()){
                                //permiso de almacenamiento aceptada
                                fotoGaleria();
                            }
                            else {
                                //no permitid la solicitud de almacenamiento
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void requestCamaraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void fotoGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void fotoCamara() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Título de la imagen");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de la imagen");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkCamaraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result && result1;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, storagePermissions,STORAGE_REQUEST_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this,"Por favor espere...", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void findAddress() {

        //buscar direccion, pais, provincia, ciudad
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitud, longitud,1);

            String direccion = addresses.get(0).getAddressLine(0); //direccion completa
            String ciudad = addresses.get(0).getLocality();
            String provincia = addresses.get(0).getAdminArea();
            String pais = addresses.get(0).getCountryName();

            paisET.setText(pais);
            provinciaET.setText(provincia);
            ciudadET.setText(ciudad);
            direccionET.setText(direccion);

        }
        catch (Exception e){
            Toast.makeText(this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Localizacion detectada
        latitud = location.getLatitude();
        longitud = location.getLongitude();

        findAddress();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        //gps localizacion desactivar
        Toast.makeText(this, "Por favor enceder el GPS", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permiso permitida
                        detectLocation();
                    }
                    else {
                        //permiso denegado
                        Toast.makeText(this,"Es necesario el permiso de localización", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permiso permitida
                        fotoCamara();

                    }
                    else {
                        //permiso denegado
                        Toast.makeText(this,"Es necesario los permisos de la camara", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permiso permitida
                        fotoGaleria();
                    }
                    else {
                        //permiso denegado
                        Toast.makeText(this,"Es necesario el permiso de almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){

                //Obtener la imagen
                image_uri = data.getData();

                //enviar la imagen a IMAGEVIEW
                perfilIV.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                //enviar la imagen a IMAGEVIEW
                perfilIV.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}