package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fullcolordesign.R;
import com.example.fullcolordesign.adapters.AdapterShop;
import com.example.fullcolordesign.models.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUsuarioActivity extends AppCompatActivity {

    private TextView nombreTV, emailTV, phoneTV, tabShopsTv, tabOrdersTv;
    private RelativeLayout shopsRl, ordersRl;
    private ImageButton cerrarsesionIB, editarperfilIB;
    private ImageView perfilIV;
    private RecyclerView shopsRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_usuario);

        nombreTV = findViewById(R.id.nombreTV);
        emailTV = findViewById(R.id.emailTV);
        phoneTV = findViewById(R.id.phoneTV);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        shopsRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.ordersRl);
        cerrarsesionIB = findViewById(R.id.cerrarsesionIB);
        editarperfilIB = findViewById(R.id.editarperfilIB);
        perfilIV = findViewById(R.id.perfilIV);
        shopsRv = findViewById(R.id.shopsRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        
        //inicio show tiendas ui
        showShopsUI();

        cerrarsesionIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hacer fuera de línea
                makeMeOffline();
            }
        });

        editarperfilIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abrir activity editar perfil
                startActivity(new Intent(MainUsuarioActivity.this, EditarPerfilUsuarioActivity.class));
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load Ordenes/Pedidos
                showOrdersUI();
            }
        });

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load Ordenes/Pedidos
                showShopsUI();
            }
        });
    }

    private void showShopsUI() {

        //show productos ui y hide ordenes ui
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {

        //show ordenes ui y hode productos ui
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private void makeMeOffline() {
        //después de iniciado la sesion, hacer usuario online
        progressDialog.setMessage("Cerrar sesion");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //subir valores a la base
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //actualizacion con exito
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //falla en la actualizacion
                        progressDialog.dismiss();
                        Toast.makeText(MainUsuarioActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainUsuarioActivity.this, LoginActivity.class));
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
                            String name = "" + ds.child("nombre").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("telefono").getValue();
                            String profileImage = "" + ds.child("imagenPerfil").getValue();
                            String tipoCuenta = "" + ds.child("tipoCuenta").getValue();
                            String city = "" + ds.child("ciudad").getValue();
                            
                            nombreTV.setText(name);
                            emailTV.setText(email);
                            phoneTV.setText(phone);
                            
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(perfilIV);
                            }
                            catch (Exception e){
                                perfilIV.setImageResource(R.drawable.ic_person_gray);
                            }
                            
                            loadShops(city);
                            

                            nombreTV.setText(name);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadShops(final String city) {
        shopList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("tipoCuenta").equalTo("Vendedor")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        shopList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopcity = ""+ds.child("ciudad").getValue();

                            if (shopcity.equals(city)){
                                shopList.add(modelShop);
                            }
                        }
                        adapterShop = new AdapterShop(MainUsuarioActivity.this, shopList);
                        shopsRv.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}