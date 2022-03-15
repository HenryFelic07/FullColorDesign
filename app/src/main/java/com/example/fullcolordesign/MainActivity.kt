package com.example.fullcolordesign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var mainBotonIniciarSesion= findViewById<Button>(R.id.mainButtonIniciarSesion)
        var mainBotonRegistrarse= findViewById<Button>(R.id.mainButtonRegistrarse)
        var mainEditTxtEmail= findViewById<EditText>(R.id.mainEditTextEmail)
        var mainEditTxtPassword= findViewById<EditText>(R.id.mainEditTextPassword)
        var mainTextFaildEmail = findViewById<TextInputLayout>(R.id.mainTextFieldEmail)

        mainBotonIniciarSesion.setOnClickListener(){
            if (mainEditTxtEmail.text.toString().trim() == "")
            {
                mainTextFaildEmail.error= getString(R.string.error)
            } else{ mainTextFaildEmail.error= null

                var intentPrincipal = Intent(this, PrincipalActivity:: class.java)
                startActivity(intentPrincipal)

            }
        }

        mainBotonRegistrarse.setOnClickListener(){

                var intent = Intent(this, Registrar:: class.java)
                startActivity(intent)

        }


    }
}