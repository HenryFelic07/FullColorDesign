package com.example.fullcolordesign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var mainBotonIngresar= findViewById<Button>(R.id.mainButtonIngresar)
        var mainEditTxtEmail= findViewById<EditText>(R.id.mainEditTextEmail)
        var mainEditTxtPassword= findViewById<EditText>(R.id.mainEditTextPassword)
        var mainTextFaildEmail = findViewById<TextInputLayout>(R.id.mainTextFieldEmail)

        mainBotonIngresar.setOnClickListener(){
            if (mainEditTxtEmail.text.toString().trim() == "")
            {
                mainTextFaildEmail.error= getString(R.string.error)
            } else{ mainTextFaildEmail.error= null }
        }


    }
}