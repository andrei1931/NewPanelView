package com.example.pv_menu

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class FcmNotificationSender {

    fun sendNotification(serverKey: String, title: String, message: String, token: String) {
        val url = "https://fcm.googleapis.com/v1/projects/[senzori-56d6b]/messages:send"
        val json = JSONObject()
        json.put("message", JSONObject().put("token", token).put("notification", JSONObject().put("title", title).put("body", message)))
        val body = json.toString()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), body))
            .addHeader("Authorization", "Bearer $serverKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Răspunsul HTTP a fost primit
                if (response.isSuccessful) {
                    // Notificarea a fost trimisă cu succes
                } else {
                    // Notificarea nu a fost trimisă cu succes
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // A apărut o eroare în timpul trimiterea cererii HTTP
            }
        })
    }
}
