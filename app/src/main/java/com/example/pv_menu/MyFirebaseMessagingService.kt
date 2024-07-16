package com.example.pv_menu

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMToken", "onNewToken called")
        saveTokenToSharedPreferences(token)
        Log.d("FCMToken", "Token saved to SharedPreferences: $token")
        sendTokenToServer(token)
        Log.d("FCMToken", "Token sent to server: $token")
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            saveNotificationToFirestore(notification.title, notification.body)
            createNotification(notification.title, notification.body)
        }
        Log.d("FCM", "From: ${remoteMessage.from}")
        Log.d("FCM", "Notification Message Body: ${remoteMessage.notification?.body}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }
    }

    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fcm_token", token)
        editor.apply()
    }

    private fun createNotification(title: String?, body: String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notify)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@MyFirebaseMessagingService, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(0, builder.build())
        }
    }
    private fun saveNotificationToFirestore(title: String?, body: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val notification = hashMapOf(
                "title" to title,
                "body" to body,
                "timestamp" to System.currentTimeMillis()

                )

            db.collection("profiles").document(userId).collection("notifications")
                .add(notification)
                .addOnSuccessListener { documentReference ->
                    Log.d("FCM", "Notification saved with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("FCM", "Error adding notification", e)
                }
        }
    }
    private fun sendTokenToServer(token: String) {
        val client = OkHttpClient()
        val url = "http://10.111.11.21:8080/api/tokens"
        val json = """{"token":"$token"}"""
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        Log.d("MyFirebaseMessaging", "Sending token to server: $token")

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("MyFirebaseMessaging", "Unexpected response code: ${response.code}")
                    throw IOException("Unexpected code $response")
                } else {
                    Log.d("MyFirebaseMessaging", "Token successfully sent to server")
                }
            }
        } catch (e: IOException) {
            Log.e("MyFirebaseMessaging", "Error sending token to server", e)
        }
    }
}
