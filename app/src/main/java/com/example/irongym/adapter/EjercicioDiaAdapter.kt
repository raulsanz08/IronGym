package com.example.irongym.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.irongym.R
import com.example.irongym.entity.EjercicioDia

class EjercicioDiaAdapter(private val listaEjercicios: List<EjercicioDia>) :
    RecyclerView.Adapter<EjercicioDiaAdapter.EjercicioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_linea_ejercicio, parent, false)
        return EjercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EjercicioViewHolder, position: Int) {
        val ejercicio = listaEjercicios[position]
        holder.tvLinea.text = "${ejercicio.nombre} -> ${ejercicio.repeticiones}"
    }

    override fun getItemCount(): Int = listaEjercicios.size

    class EjercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLinea: TextView = itemView.findViewById(R.id.tvLineaEjercicio)
    }
}
