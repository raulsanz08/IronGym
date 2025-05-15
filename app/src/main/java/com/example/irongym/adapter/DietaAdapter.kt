package com.example.irongym.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.DetalleDietaActivity
import com.example.irongym.R
import com.example.irongym.entity.Dieta

class DietaAdapter(private var listaDietas: List<Dieta>) :
    RecyclerView.Adapter<DietaAdapter.DietaViewHolder>() {

    private var allDietas: List<Dieta> = listaDietas.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dieta, parent, false)
        return DietaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DietaViewHolder, position: Int) {
        val dieta = listaDietas[position]
        holder.tvNombre.text = dieta.nombre
        holder.tvDescripcion.text = dieta.descripcion

        Glide.with(holder.itemView.context)
            .load(dieta.imagenUrl)
            .into(holder.ivDieta)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleDietaActivity::class.java)
            intent.putExtra("DIETA_ID", dieta.id)
            intent.putExtra("imagenUrl", dieta.imagenUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaDietas.size


    fun updateData(newListaDietas: List<Dieta>) {
        listaDietas = newListaDietas
        allDietas = newListaDietas.toList()
        notifyDataSetChanged()
    }


    fun filter(query: String) {
        listaDietas = if (query.isEmpty()) {
            allDietas
        } else {
            allDietas.filter { it.nombre.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    class DietaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDieta: ImageView = itemView.findViewById(R.id.ivDieta)
        val tvNombre: TextView = itemView.findViewById(R.id.tvDietaTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDietaDescripcion)
    }
}
