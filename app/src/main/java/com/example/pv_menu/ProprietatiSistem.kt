package com.example.pv_menu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProprietatiSistem : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProprietateAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proprietati_sistem)
        fab = findViewById(R.id.floatingActionButton2)

        recyclerView = findViewById(R.id.rvProp)
        adapter = ProprietateAdapter(mutableListOf())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val idJs = intent.getStringExtra("idJs")

        if (idJs != null) {
            getProprietatiFromFirebase(idJs)
        }
        fab.setOnClickListener {
            val intent = Intent(this, AdaugareProprietate::class.java)
            intent.putExtra("idJs", idJs)
            startActivity(intent)
        }
        adapter.setOnItemClickListener { position ->
            showDeleteConfirmationDialog(position)
        }

    }

    private fun getProprietatiFromFirebase(idJs: String?) {
        val userCurent = FirebaseAuth.getInstance().currentUser
        if (userCurent != null && idJs != null) {
            val userId = userCurent.uid
            val sistemRef = db.collection("profiles").document(userId).collection("sis").document(idJs)

            sistemRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                val proprietati = mutableListOf<Proprietate>()
                snapshot?.data?.forEach { (key, value) ->
                    val cheie = key
                    val valoare = value.toString()
                    proprietati.add(Proprietate(cheie, valoare))
                }

                adapter.setProprietati(proprietati)
            }
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Confirmare Ștergere")
            setMessage("Sigur doriți să ștergeți această proprietate?")
            setPositiveButton("Da") { dialog, _ ->
                deleteProprietate(position)
                dialog.dismiss()
            }
            setNegativeButton("Nu") { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.create().show()
    }

    private fun deleteProprietate(position: Int) {
        val propToDelete = adapter.getItem(position)
        adapter.removeProprietate(position)

        val idJs = intent.getStringExtra("idJs")
        if (idJs != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val sistemRef = db.collection("profiles").document(userId).collection("sis").document(idJs)
                val updates = hashMapOf<String, Any>(
                    propToDelete.cheie to FieldValue.delete()
                )
                sistemRef.update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Proprietate ștearsă cu succes", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Eroare la ștergere: ${e.message}", Toast.LENGTH_SHORT).show()
                        // Readaugă proprietatea în listă în caz de eșec la ștergere
                        adapter.addProprietate(propToDelete.cheie, propToDelete.valoare)
                    }
            }
        }
    }
}
