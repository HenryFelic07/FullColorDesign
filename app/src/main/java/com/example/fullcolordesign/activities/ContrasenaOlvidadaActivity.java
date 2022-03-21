package com.example.fullcolordesign.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fullcolordesign.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ContrasenaOlvidadaActivity extends AppCompatActivity {

    private ImageButton backB;
    private EditText mailET;
    private Button recuperarloginB;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena_olvidada);

        backB = findViewById(R.id.backB);
        mailET = findViewById(R.id.mailET);
        recuperarloginB = findViewById(R.id.recuperarloginB);

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

        recuperarloginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarPassword();
            }
        });
    }

    private String email;
    private void recuperarPassword() {
        email = mailET.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email inválido",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Enviando instrucciones para restablecer la contraseña");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //instruciones enviada
                        progressDialog.dismiss();
                        Toast.makeText(ContrasenaOlvidadaActivity.this, "Instrucciones para restablecer la contraseña enviada a su correo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //instruccion enviada fallida
                        progressDialog.dismiss();
                        Toast.makeText(ContrasenaOlvidadaActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });

    }
}