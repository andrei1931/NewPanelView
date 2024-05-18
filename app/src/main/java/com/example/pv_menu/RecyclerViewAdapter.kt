package com.example.pv_menu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pv_menu.ui.dashboard.DashboardFragment

class RecyclerViewAdapter(private var sistemList: List<Sistem>) :
    RecyclerView.Adapter<RecyclerViewAdapter.SistemViewHolder>() {

    class SistemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentIdTextView: TextView = itemView.findViewById(R.id.textView)
        val locatieTextView: TextView = itemView.findViewById(R.id.textView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SistemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return SistemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SistemViewHolder, position: Int) {
        val currentItem = sistemList[position]
        holder.documentIdTextView.text = currentItem.documentId
        holder.locatieTextView.text = currentItem.locatie

        holder.itemView.setOnClickListener {
            val id = currentItem.documentId
            val locatie = currentItem.locatie
            val bundle = bundleOf("id" to id, "locatie" to locatie)

            // Save id and locatie to SharedPreferences
            val sharedPreferences = holder.itemView.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("idJs", id)
                putString("locatieJs", locatie)
                apply()
            }

            holder.itemView.findNavController().navigate(R.id.action_to_dashboardFragment, bundle)
        }
    }

    fun updateData(newSistemList: List<Sistem>) {
        this.sistemList = newSistemList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return sistemList.size
    }
}
