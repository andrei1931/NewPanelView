package com.example.pv_menu.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.pv_menu.RecyclerViewAdapter
import com.example.pv_menu.Sistem
import com.example.pv_menu.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = FirebaseFirestore.getInstance()
        recyclerViewAdapter = RecyclerViewAdapter(emptyList())
        binding.SF.adapter = recyclerViewAdapter

        // Setare LayoutManager pentru RecyclerView
        binding.SF.layoutManager = LinearLayoutManager(context)

        // Request a layout update for the RecyclerView
        binding.SF.requestLayout()

        val userCurent = FirebaseAuth.getInstance().currentUser
        if (userCurent != null) {
            val userId = userCurent.uid
            db.collection("profiles")
                .document(userId)
                .collection("sis")
                .get()
                .addOnSuccessListener { result ->
                    // Log statement to check if data is being fetched correctly
                    Log.d("HomeFragment", "Data retrieved successfully")

                    val sistemList = mutableListOf<Sistem>()

                    for (document in result) {
                        sistemList.add(
                            Sistem(
                                documentId = document.id,
                                locatie = document.getString("locatie") ?: ""
                            )

                        )
                        Log.d("HomeFragment", "${document.id} => ${document.data}")
                    }

                    recyclerViewAdapter?.updateData(sistemList)
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeFragment", "Error getting documents: ", exception)
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}