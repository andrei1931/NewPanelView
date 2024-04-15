package com.example.pv_menu

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usrEditText: EditText
    private lateinit var pwdEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
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
        usrEditText = findViewById(R.id.editTextTextPersonName2)
        pwdEditText = findViewById(R.id.editTextTextPassword2)
        signInButton = findViewById(R.id.button)
        signUpButton = findViewById(R.id.button2)


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        signInButton.setOnClickListener {
            val usr = usrEditText.text.toString()
            val pwd = pwdEditText.text.toString()

            signIn(usr, pwd)
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, Inregistrare::class.java))
        }


    }
    private fun signIn(usr: String?, pwd: String?) {
        if (usr.isNullOrEmpty() || pwd.isNullOrEmpty()) {
            Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(usr!!.trim(), pwd!!.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val editor = getSharedPreferences("MyData", Context.MODE_PRIVATE).edit()
            editor.putString("username", user.displayName)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}