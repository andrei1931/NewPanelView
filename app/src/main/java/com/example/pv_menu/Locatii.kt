package com.example.pv_menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pv_menu.databinding.FragmentLocatiiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class Locatii : Fragment() {

    private var _binding: FragmentLocatiiBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocatiiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = FirebaseFirestore.getInstance()

        val mapView = binding.mapView

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("profiles").document(userId).collection("sis")
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (document in result) {
                            var coordinates: GeoPoint? = null
                            val geoPoint = document.getGeoPoint("coordonate")
                            if (geoPoint != null) {
                                coordinates = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                            } else {
                                val coordinatesString = document.getString("coordonate")
                                if (coordinatesString != null) {
                                    val matches = Regex("""([0-9]+\.[0-9]+)°([NS])[^\d\.]*(?:\s*([0-9]+\.[0-9]+)°([EW]))""")
                                        .find(coordinatesString)
                                    matches?.let {
                                        val (latDegrees, latDirection, lonDegrees, lonDirection) = it.destructured
                                        val latitude = latDegrees.toDoubleOrNull()
                                        val longitude = lonDegrees.toDoubleOrNull()

                                        if (latitude != null && longitude != null) {
                                            val finalLatitude = if (latDirection == "N") latitude else -latitude
                                            val finalLongitude = if (lonDirection == "E") longitude else -longitude

                                            coordinates = GeoPoint(finalLatitude, finalLongitude)
                                            Log.d("Locatii", "Coordonatele: $finalLatitude, $finalLongitude")
                                        } else {
                                            Log.e("Harta", "Invalid coordinates found for document: ${document.id}")
                                        }
                                    }
                                }
                            }

                            if (coordinates != null) {
                                val locationName = document.getString("locatie")
                                val marker = Marker(mapView)
                                marker.position = coordinates
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = locationName ?: "Unknown Location"

                                mapView.overlays.add(marker)
                                var clickCount = 0

                                marker.setOnMarkerClickListener { marker, _ ->
                                    clickCount++

                                    if (clickCount == 1) {
                                        marker.showInfoWindow()
                                    } else if (clickCount == 2) {
                                        val intent = Intent(activity, ProprietatiSistem::class.java)
                                        intent.putExtra("idJs", document.id)  // Pass the document ID
                                        startActivity(intent)
                                        clickCount = 0
                                    }

                                    true
                                }
                            }
                        }

                        if (mapView.overlays.isNotEmpty()) {
                            val lastOverlay = mapView.overlays.last()
                            if (lastOverlay is Marker) {
                                mapView.controller.setCenter(lastOverlay.position)
                                mapView.controller.setZoom(7.0)
                            }
                        }
                    }
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
