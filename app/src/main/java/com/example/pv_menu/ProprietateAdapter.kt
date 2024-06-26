package com.example.pv_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProprietateAdapter(private val proprietati: MutableList<Proprietate>) :
    RecyclerView.Adapter<ProprietateAdapter.ViewHolder>() {

    private var listener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.properties_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proprietate = proprietati[position]
        holder.textKey.text = proprietate.cheie
        holder.textValue.text = proprietate.valoare

        // Setează OnClickListener pentru elementul din RecyclerView
        holder.itemView.setOnClickListener {
            listener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return proprietati.size
    }

    fun setProprietati(proprietatiList: MutableList<Proprietate>) {
        proprietati.clear()
        proprietati.addAll(proprietatiList)
        notifyDataSetChanged()
    }

    fun addProprietate(cheie: String, valoare: String) {
        proprietati.add(Proprietate(cheie, valoare))
        notifyItemInserted(proprietati.size - 1)
    }

    fun removeProprietate(position: Int) {
        proprietati.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textKey: TextView = itemView.findViewById(R.id.text_key)
        val textValue: TextView = itemView.findViewById(R.id.text_value)
    }

    fun getItem(position: Int): Proprietate {
        return proprietati[position]
    }
}
