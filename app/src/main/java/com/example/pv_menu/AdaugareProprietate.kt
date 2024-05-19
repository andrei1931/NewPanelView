package com.example.pv_menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdaugareProprietate : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaugare_proprietate)

        // Referințe către elementele din layout
        val proprietateEditText = findViewById<EditText>(R.id.Proprietate)
        val valoareEditText = findViewById<EditText>(R.id.Valoare)
        val addButton = findViewById<Button>(R.id.button)
        val idJs = intent.getStringExtra("idJs")
        // Ascultă evenimentul de click al butonului "Add"
        addButton.setOnClickListener {
            // Obține id-ul utilizatorului curent
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // Verifică dacă utilizatorul este autentificat
            if (userId != null) {
                // Obține valorile introduse în câmpurile EditText
                val proprietate = proprietateEditText.text.toString()
                val valoare = valoareEditText.text.toString()

                // Crează un nou obiect de tip HashMap pentru a adăuga noile date
                val newData = hashMapOf<String, Any>(
                    proprietate to valoare
                )


                if (idJs != null) {
                    val sistemRef =
                        db.collection("profiles").document(userId).collection("sis").document(idJs)


                    sistemRef.update(newData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Added succesfully", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}
