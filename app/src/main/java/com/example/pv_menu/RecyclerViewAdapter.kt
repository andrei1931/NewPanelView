package com.example.pv_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
    }
    fun updateData(newSistemList: List<Sistem>) {
        this.sistemList = newSistemList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return sistemList.size
    }
}

