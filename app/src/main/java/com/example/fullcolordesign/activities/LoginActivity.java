package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fullcolordesign.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //Interface Usuario Vista
    private EditText mailET, passET;
    private TextView forgotTV, noCuentaTV;
    private Button iniloginB;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicia Inteface usuario vista
        mailET = findViewById(R.id.mailET);
        passET = findViewById(R.id.passET);
        forgotTV = findViewById(R.id.forgotTV);
        noCuentaTV = findViewById(R.id.noCuentaTV);
        iniloginB = findViewById(R.id.iniloginB);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);


        //No tiene cuenta
        noCuentaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroUsuarioActivity.class));
            }
        });


        //Olvido su cotraseña
        forgotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ContrasenaOlvidadaActivity.class));
            }
        });

        iniloginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private String email, password;
    private void loginUser() {
        email = mailET.getText().toString().trim();
        password = passET.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Patrones de email inválidos",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Ingrese Password",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Iniciando sesión");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //iniciar sesión con éxito
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Iniciando sesion fallida
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
    }

    private void makeMeOnline() {
        //después de iniciado la sesion, hacer usuario online
        progressDialog.setMessage("Comprobando usuario");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //subir valores a la base
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //actualizacion con exito
                        checkUserTipo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //falla en la actualizacion
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUserTipo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String tipoCuenta = ""+ds.child("tipoCuenta").getValue();
                            if (tipoCuenta.equals("Vendedor")){
                                //usuario es vendedor
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainVendedorActivity.class));
                                finish();
                            }
                            else {
                                //usuario es comprador
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainUsuarioActivity.class));
                                finish();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}