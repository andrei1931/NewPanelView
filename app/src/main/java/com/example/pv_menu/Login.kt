package com.example.pv_menu

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val buton: Button = findViewById(R.id.button)
        val user_name: EditText = findViewById(R.id.editTextTextPersonName2)
        val parola: EditText = findViewById(R.id.editTextTextPassword2)
        val buton2: Button = findViewById(R.id.button2)


        buton.setOnClickListener {
            val usr = user_name.text.toString()
            val pwd = parola.text.toString()
            aut(usr, pwd)
        }
        buton2.setOnClickListener {
            Intent(this, Inregistrare::class.java).also {
                startActivity(it)
            }
        }
    }
        fun aut(usr: String, pwd: String) {
            if (usr.isEmpty() || pwd.isEmpty()) {
                val myToast =
                    Toast.makeText(
                        applicationContext,
                        "Introdu emailul și parola",
                        Toast.LENGTH_SHORT
                    )
                myToast.show()
            } else {
                auth.signInWithEmailAndPassword(usr, pwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            Intent(this, MainActivity::class.java).also {
                                startActivity(it)

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Email sau parola gresita",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }
            }
        }

    }
