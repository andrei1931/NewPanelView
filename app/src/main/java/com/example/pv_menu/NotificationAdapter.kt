package com.example.pv_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pv_menu.R

class NotificationAdapter(private var notifications: List<Map<String, Any>>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.notification_title)
        val body: TextView = view.findViewById(R.id.notification_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification["title"] as? String ?: "No Title"
        holder.body.text = notification["body"] as? String ?: "No Body"
    }

    override fun getItemCount() = notifications.size

    fun setNotifications(notifications: List<Map<String, Any>>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }
}