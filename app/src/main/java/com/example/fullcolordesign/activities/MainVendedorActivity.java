package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fullcolordesign.Constantes;
import com.example.fullcolordesign.adapters.AdapterProductVendedor;
import com.example.fullcolordesign.models.ModelProduct;
import com.example.fullcolordesign.R;
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

public class MainVendedorActivity extends AppCompatActivity {

    private TextView nombreTV, shopNameTV, emailTV, tabProductsTv, tabOrdersTv, filteredProductsTv;
    private ImageButton cerrarsesionIB, editarperfilIB, addProductB, filterProductBtn;
    private EditText searchProductEt;
    private ImageView perfilIV;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productsRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductVendedor adapterProductVendedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vendedor);

        nombreTV = findViewById(R.id.nombreTV);
        shopNameTV = findViewById(R.id.shopNameTV);
        emailTV = findViewById(R.id.emailTV);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        cerrarsesionIB = findViewById(R.id.cerrarsesionIB);
        editarperfilIB = findViewById(R.id.editarperfilIB);
        addProductB = findViewById(R.id.addProductB);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        perfilIV = findViewById(R.id.perfilIV);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        productsRv = findViewById(R.id.productsRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        showProductsUI();

        //buscar
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductVendedor.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                startActivity(new Intent(MainVendedorActivity.this, EditarPerfilVendedorActivity.class));

            }
        });

        addProductB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abrir activity añadir producto perfil
                startActivity(new Intent(MainVendedorActivity.this, AddProductActivity.class));

            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load productos
                showProductsUI();
            }
        });

        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load Ordenes/Pedidos
                showOrdersUI();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainVendedorActivity.this);
                builder.setTitle("Escoga Categoria:")
                        .setItems(Constantes.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constantes.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("Todo")){
                                    //load all
                                    loadAllProducts();
                                }
                                else {
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                .show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {

        productList = new ArrayList<>();

        //get all productos
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();

                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        adapterProductVendedor = new AdapterProductVendedor(MainVendedorActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductVendedor);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        //get all productos
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        adapterProductVendedor = new AdapterProductVendedor(MainVendedorActivity.this, productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductVendedor);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        //show productos ui y hide ordenes ui
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show ordenes ui y hode productos ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                        Toast.makeText(MainVendedorActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null){
            startActivity(new Intent(MainVendedorActivity.this, LoginActivity.class));
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
                            //obtener data desde la db
                            String name = "" + ds.child("nombre").getValue();
                            String tipoCuenta = "" + ds.child("tipoCuenta").getValue();
                            String email = "" + ds.child("email").getValue();
                            String shopName = "" + ds.child("nombrePilonera").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();

                            //
                            nombreTV.setText(name);
                            shopNameTV.setText(shopName);
                            emailTV.setText(email);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(perfilIV);
                            }
                            catch (Exception e){
                                perfilIV.setImageResource(R.drawable.ic_store_gray);
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}