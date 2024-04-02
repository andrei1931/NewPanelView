package com.example.pv_menu

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocatiiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inițializarea FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        // Inițializarea MapView și configurația pentru OSMDroid
        val mapView = binding.mapView
        // Restul codului rămâne neschimbat

        // Inițializarea Firestore și crearea interogării pentru colecția "sis"
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("profiles").document(userId).collection("sis")
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (document in result) {
                            var coordinates: GeoPoint? = null
                            // Procesează fiecare document din colecție și adaugă un marker pe hartă
                            // Codul pentru procesarea coordonatelor și adăugarea markerelor pe hartă
                            // rămâne neschimbat
                            val geoPoint = document.getGeoPoint("coordonate")
                            if (geoPoint != null) {
                                val osmdroidGeoPoint = com.google.firebase.firestore.GeoPoint(
                                    geoPoint.latitude,
                                    geoPoint.longitude
                                )
                                coordinates = org.osmdroid.util.GeoPoint(geoPoint!!.latitude, geoPoint.longitude)

                            } else {
                                // If that fails, try to retrieve 'coordonate' as a String
                                val coordinatesString = document.getString("coordonate")
                                if (coordinatesString != null && coordinatesString is String) {
                                    val matches =
                                        Regex("""([0-9]+\.[0-9]+)°([NS])[^\d\.]*(?:\s*([0-9]+\.[0-9]+)°([EW]))""").find(
                                            coordinatesString
                                        )

                                    matches?.let {
                                        val (latDegrees, latDirection, lonDegrees, lonDirection) = matches.destructured
                                        val latitude = latDegrees.toDoubleOrNull()
                                        val longitude = lonDegrees.toDoubleOrNull()

                                        if (latitude != null && longitude != null) {
                                            // Adjust latitude and longitude based on direction
                                            val finalLatitude =
                                                if (latDirection == "N") latitude else -latitude
                                            val finalLongitude =
                                                if (lonDirection == "E") longitude else -longitude

                                            coordinates = org.osmdroid.util.GeoPoint(
                                                finalLatitude,
                                                finalLongitude
                                            )

                                            Log.d("Locatii", "Coordonatele: $finalLatitude, $finalLongitude")
                                        } else {
                                            Log.e(
                                                "Harta",
                                                "Invalid coordinates found for document: ${document.id}"
                                            )
                                        }
                                    }
                                }
                            }

                            if (coordinates != null) {
                                val locationName = document.getString("locatie")
                                val marker = Marker(mapView)
                                marker.position = coordinates
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title =
                                    locationName ?: "Unknown Location" // Provide a default name if null
                                mapView.overlays.add(marker)
                            }
                        }

                        // Adjust map center and zoom

// Adjust map center and zoom
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
