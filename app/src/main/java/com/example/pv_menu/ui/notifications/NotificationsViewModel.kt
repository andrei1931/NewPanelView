package com.example.pv_menu.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationsViewModel (application: Application) : AndroidViewModel(application) {

    private val notifications = MutableLiveData<List<Map<String, Any>>>()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadNotifications()
    }

    fun getNotifications(): LiveData<List<Map<String, Any>>> {
        return notifications
    }

    private fun loadNotifications() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            listenerRegistration = db.collection("profiles").document(userId).collection("notifications")

                .addSnapshotListener { value, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }

                    val notificationList = mutableListOf<Map<String, Any>>()
                    if (value != null) {
                        for (document in value.documents) {
                            document.data?.let { data ->
                                notificationList.add(data)
                            }
                        }
                    }
                    val sortedNotifications = notificationList.sortedByDescending {
                        it["timestamp"] as? Long ?: 0L
                    }

                    notifications.value = sortedNotifications
                }
        }
    }
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}