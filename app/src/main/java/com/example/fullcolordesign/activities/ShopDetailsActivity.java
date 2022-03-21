package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fullcolordesign.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv, phoneTv, openCloseTv, deliveryFreeTv, addressTv, filteredProductsTv, emailTV;
    private ImageButton callBtn, mapBtn, backBtn, filterProductBtn, cartBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String shopUid;
    private String myLatitude, mylongitud;
    private String shopName, shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        emailTV = findViewById(R.id.emailTV);
        phoneTv = findViewById(R.id.phoneTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFreeTv = findViewById(R.id.deliveryFreeTv);
        addressTv = findViewById(R.id.addressTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        backBtn = findViewById(R.id.backBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        cartBtn = findViewById(R.id.cartBtn);

        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hacer fuera de línea

            }
        });
    }

    private void loadShopProducts() {
    }

    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = ""+dataSnapshot.child("nombre").getValue();
                shopName = ""+dataSnapshot.child("shopName").getValue();
                shopEmail = ""+dataSnapshot.child("shopEmail").getValue();
                shopPhone = ""+dataSnapshot.child("shopPhone").getValue();
                shopLatitude = ""+dataSnapshot.child("latitud").getValue();
                shopLongitude = ""+dataSnapshot.child("longitud").getValue();
                String deliveryFree = "" +dataSnapshot.child("deliveryFree").getValue();
                String profileImage = "" +dataSnapshot.child("imagenPerfil").getValue();
                String shopOpen = "" +dataSnapshot.child("shopOpen").getValue();

                shopNameTv.setText(shopName);
                emailTV.setText(shopEmail);
                deliveryFreeTv.setText(deliveryFree);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if (shopOpen.equals("true")){
                    openCloseTv.setText("Open");
                }
                else {
                    openCloseTv.setText("Closed");
                }
                try {
                    Picasso.get().load(profileImage).into(shopIv);
                }
                catch (Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyInfo() {
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
                            myLatitude = ""+ds.child("latitud").getValue();
                            mylongitud = "" +ds.child("longitud").getValue();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}