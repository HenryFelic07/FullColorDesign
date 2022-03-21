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
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegistroVendedorActivity extends AppCompatActivity implements LocationListener {

    private ImageButton backB, gpsB;
    private ImageView perfilIV;
    private EditText nameET, piloneraET, telefonoET, envioET, paisET,
            provinciaET, ciudadET, direccionET, mailET, passET, confpassET;
    private Button registrarB;

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

    //uri imagen foto
    private Uri image_uri;

    private double latitud =0.0, longitud =0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_vendedor);

        backB = findViewById(R.id.backB);
        gpsB = findViewById(R.id.gpsB);
        perfilIV = findViewById(R.id.perfilIV);
        nameET = findViewById(R.id.nameET);
        piloneraET = findViewById(R.id.piloneraET);
        telefonoET = findViewById(R.id.telefonoET);
        envioET = findViewById(R.id.envioET);
        paisET = findViewById(R.id.paisET);
        provinciaET = findViewById(R.id.provinciaET);
        ciudadET = findViewById(R.id.ciudadET);
        direccionET = findViewById(R.id.direccionET);
        mailET =findViewById(R.id.mailET);
        passET = findViewById(R.id.passET);
        confpassET = findViewById(R.id.confpassET);
        registrarB = findViewById(R.id.registrarB);

        //init permission array
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);


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

        perfilIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //foto usuario
                showImagePickDialog();

            }
        });

        registrarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Registrar usuario
                inputData();
            }
        });
    }

    private String nombre, nombrePilonera, telefono, deliveryFree, pais, provincia, ciudad, direccion, email, password, confirpassword;
    private void inputData() {
        //input datos
        nombre = nameET.getText().toString().trim();
        nombrePilonera = piloneraET.getText().toString().trim();
        telefono = telefonoET.getText().toString().trim();
        deliveryFree = envioET.getText().toString().trim();
        pais = paisET.getText().toString().trim();
        provincia = provinciaET.getText().toString().trim();
        ciudad = ciudadET.getText().toString().trim();
        direccion = direccionET.getText().toString().trim();
        email = mailET.getText().toString().trim();
        password = passET.getText().toString().trim();
        confirpassword = confpassET.getText().toString().trim();

        //validar datos
        if (TextUtils.isEmpty(nombre)){
            Toast.makeText(this,"Ingrese su nombre completo ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nombrePilonera)){
            Toast.makeText(this, "Ingrese el nombre de la pilonera ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(telefono)){
            Toast.makeText(this,"Ingrese su número de teléfono ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deliveryFree)){
            Toast.makeText(this,"Ingrese delivery free ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitud == 0.0 || latitud == 0.0){
            Toast.makeText(this,"Por favor click en el botón GPS para detectar su localización ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Inválido patrones email ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<8){
            Toast.makeText(this,"La contraseña debe contener 6 caracteres como mínimo ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirpassword)) {
            Toast.makeText(this, "La contraseña no coincide ", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();

    }

    private void createAccount() {
        progressDialog.setMessage("Creando Cuenta ");
        progressDialog.show();

        //crear cuenta
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //cuenta creada
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //fallo creacion cuenta
                        progressDialog.dismiss();
                        Toast.makeText(RegistroVendedorActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Guardando información de la cuenta");

        String timestamp = ""+System.currentTimeMillis();

        if (image_uri==null){
            //guarda informacion sin la foto

            //configurar datos para guardar
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+ firebaseAuth.getUid());
            hashMap.put("email",""+ email);
            hashMap.put("nombre", ""+ nombre);
            hashMap.put("nombrePilonera", ""+ nombrePilonera);
            hashMap.put("telefono", ""+ telefono);
            hashMap.put("deliveryFree", ""+ deliveryFree);
            hashMap.put("pais", ""+ pais);
            hashMap.put("provincia", ""+ provincia);
            hashMap.put("ciudad", ""+ ciudad);
            hashMap.put("direccion", ""+ direccion);
            hashMap.put("latitud", ""+ latitud);
            hashMap.put("longitud", ""+ longitud);
            hashMap.put("timestamp", ""+ timestamp);
            hashMap.put("tipoCuenta", "Vendedor");
            hashMap.put("online", "true");
            hashMap.put("shopOpen", "true");
            hashMap.put("imagenPerfil", "");

            //guardar en la base de datos
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //actualizar base datos
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistroVendedorActivity.this, MainVendedorActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //falla en la actualizacion de la base
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistroVendedorActivity.this, MainVendedorActivity.class));
                            finish();

                        }
                    });
        }
        else {
            //guardar informacion con la foto

            //nombre y ruta de la imagen
            String filePathName = "profile_images/" + ""+firebaseAuth.getUid();
            //subir imagen
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //obtener la URL de la imagen subida
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadimageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                //configurar datos para guardar
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+ firebaseAuth.getUid());
                                hashMap.put("email",""+ email);
                                hashMap.put("nombre", ""+ nombre);
                                hashMap.put("nombrePilonera", ""+ nombrePilonera);
                                hashMap.put("telefono", ""+ telefono);
                                hashMap.put("deliveryFree", ""+ deliveryFree);
                                hashMap.put("pais", ""+ pais);
                                hashMap.put("provincia", ""+ provincia);
                                hashMap.put("ciudad", ""+ ciudad);
                                hashMap.put("direccion", ""+ direccion);
                                hashMap.put("latitud", ""+ latitud);
                                hashMap.put("longitud", ""+ longitud);
                                hashMap.put("timestamp", ""+ timestamp);
                                hashMap.put("tipoCuenta", "Vendedor");
                                hashMap.put("online", "true");
                                hashMap.put("shopOpen", "true");
                                hashMap.put("imagenPerfil", "" +downloadimageUri);//url  o la subida de imagen

                                //guardar en la base de datos
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //actualizar base datos
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistroVendedorActivity.this, MainVendedorActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //falla en la actualizacion de la base
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistroVendedorActivity.this, MainVendedorActivity.class));
                                                finish();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistroVendedorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
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

    private void fotoGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void fotoCamara(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Título de la imagen");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de la imagen");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this,"Por favor espere...", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
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

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCamaraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result && result1;
    }

    private void requestCamaraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions,CAMERA_REQUEST_CODE);
    }


    @Override
    public void onLocationChanged(Location location) {
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