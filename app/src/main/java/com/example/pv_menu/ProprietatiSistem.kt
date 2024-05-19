package com.example.pv_menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProprietatiSistem : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProprietateAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proprietati_sistem)

        recyclerView = findViewById(R.id.rvProp)
        adapter = ProprietateAdapter(mutableListOf())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val idJs = intent.getStringExtra("idJs")

        if (idJs != null) {
            getProprietatiFromFirebase(idJs)
        }
    }

    private fun getProprietatiFromFirebase(idJs: String) {
        val userCurent = FirebaseAuth.getInstance().currentUser
        if (userCurent != null) {
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
}
