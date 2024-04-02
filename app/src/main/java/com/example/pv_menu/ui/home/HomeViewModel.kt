package com.example.pv_menu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _sistemList = MutableLiveData<List<String>>()
    val sistemList: LiveData<List<String>> = _sistemList

    init {
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val userCurent = FirebaseAuth.getInstance().currentUser
        if (userCurent != null) {
            val userId = userCurent.uid
            db.collection("profiles")
                .document(userId)
                .collection("sis")
                .get()
                .addOnSuccessListener { result ->
                    val dataList = mutableListOf<String>()
                    for (document in result) {
                        val locatie = document.getString("locatie")
                        locatie?.let {
                            dataList.add(it)
                        }
                    }
                    _sistemList.value = dataList
                }
                .addOnFailureListener { exception ->
                    // Manejare eroare
                }
        }
    }
}
