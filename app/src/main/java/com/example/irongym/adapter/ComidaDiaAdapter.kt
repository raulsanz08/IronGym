package com.example.irongym.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.irongym.R
import com.example.irongym.entity.ComidaDia

class ComidaDiaAdapter(private val listaComidas: List<ComidaDia>) :
    RecyclerView.Adapter<ComidaDiaAdapter.ComidaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_linea_comida, parent, false)
        return ComidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val comida = listaComidas[position]
        holder.tvNombre.text = comida.nombre
        holder.tvDescripcion.text = comida.descripcion

        // Agrega esto para depurar
        Log.d("ComidaDiaAdapter", "Comida: ${comida.nombre} - ${comida.descripcion}")
    }


    override fun getItemCount(): Int = listaComidas.size

    class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreComida)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDetalleComida)
    }
}
