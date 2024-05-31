package com.example.pv_menu

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.text.SimpleDateFormat
import java.util.*

class Events : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var eventsAdapter: ArrayAdapter<String>
    private val db = FirebaseFirestore.getInstance()
    private var idJs: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        // Obține idJs din intent
        idJs = intent.getStringExtra("idJs")

        // Inițializează ListView și adapter-ul
        listView = findViewById(R.id.listViewEvenimente)
        eventsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = eventsAdapter

        // Citește evenimentele din Firebase dacă idJs nu este nul
        if (idJs != null) {
            Log.d("Events", "idJs: $idJs")  // Log pentru debugging
            getEventsFromFirebase(idJs!!)
        } else {
            // Tratează cazul în care idJs este nul (de exemplu, afișează un mesaj de eroare)
            showError("ID-ul sistemului fotovoltaic nu este specificat.")
        }
    }

    private fun getEventsFromFirebase(idJs: String) {
        val userCurent = FirebaseAuth.getInstance().currentUser
        if (userCurent != null) {
            val userId = userCurent.uid
            val eventsRef = db.collection("profiles").document(userId).collection("sis")
                .document(idJs).collection("evenimente")

            Log.d("Events", "Reference: ${eventsRef.path}")

            eventsRef.get()
                .addOnSuccessListener { documents ->
                    val eventsList = mutableListOf<String>()
                    for (document in documents) {
                        Log.d("Events", "Document ID: ${document.id}")
                        val eventDetails = StringBuilder()
                        for (field in document.data) {
                            val value = field.value
                            val formattedValue = when (value) {
                                is Timestamp -> SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(value.toDate())
                                else -> value.toString()
                            }
                            eventDetails.append("${field.key}: $formattedValue\n")
                        }
                        eventsList.add(eventDetails.toString().trim())
                    }
                    // Actualizează adapter-ul cu lista de evenimente
                    eventsAdapter.clear()
                    eventsAdapter.addAll(eventsList)
                    eventsAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Tratează eroarea
                    Log.e("Events", "Error getting documents: ", exception)
                    showError("Eroare la încărcarea evenimentelor: ${exception.message}")
                }
        } else {
            showError("Utilizatorul nu este autentificat.")
        }
    }

    private fun showError(message: String) {
        // Afișează un mesaj de eroare (de exemplu, folosind Toast)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
