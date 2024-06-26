package com.example.pv_menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdaugareSistem.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdaugareSistem : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_adaugare_sistem, container, false)

        val idS: EditText = view.findViewById(R.id.editTextIDSistem)
        val locatie: EditText = view.findViewById(R.id.editTextLocatie)
        val latitudine: EditText = view.findViewById(R.id.editTextLatitudine)
        val longitudine: EditText = view.findViewById(R.id.editTextLongitudine)
        val adauga: Button = view.findViewById(R.id.adaugare)
        adauga.setOnClickListener {
            adaugare(idS.text.toString(), locatie.text.toString(), latitudine.text.toString(), longitudine.text.toString())
        }

        return view
    }

    fun adaugare(idS: String, locatie: String, latitudine: String, longitudine: String) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = db.collection("profiles")
                .document(userId)
                .collection("sis")
                .document(idS)
            val data = hashMapOf(
                "locatie" to locatie,
                "coordonate" to GeoPoint(latitudine.toDouble(), longitudine.toDouble())
            )
            userRef.set(data)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Merge", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Eroare", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdaugareSistem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdaugareSistem().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
