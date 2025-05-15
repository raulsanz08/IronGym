package com.example.irongym.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.DetalleRutinaActivity
import com.example.irongym.R
import com.example.irongym.entity.Rutina

class RutinaAdapter(private var listaRutinas: List<Rutina>) :
    RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder>() {

    private var allRutinas: List<Rutina> = listaRutinas.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutinaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rutina, parent, false)
        return RutinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutinaViewHolder, position: Int) {
        val rutina = listaRutinas[position]
        holder.tvNombre.text = rutina.nombre
        holder.tvDescripcion.text = rutina.descripcion

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleRutinaActivity::class.java)
            intent.putExtra("RUTINA_ID", rutina.id)
            intent.putExtra("nombre", rutina.nombre)
            intent.putExtra("descripcion", rutina.descripcion)
            intent.putExtra("imagenUrl", rutina.imagenUrl)
            context.startActivity(intent)
        }

        Glide.with(holder.itemView.context)
            .load(rutina.imagenUrl)
            .into(holder.ivRutina)
    }

    override fun getItemCount(): Int = listaRutinas.size

    fun updateData(newListaRutinas: List<Rutina>) {
        listaRutinas = newListaRutinas
        allRutinas = newListaRutinas.toList()
        notifyDataSetChanged()
    }


    fun filter(query: String) {
        listaRutinas = if (query.isEmpty()) {
            allRutinas
        } else {
            allRutinas.filter { it.nombre.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    class RutinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRutina: ImageView = itemView.findViewById(R.id.ivRutina)
        val tvNombre: TextView = itemView.findViewById(R.id.tvRutinaTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvRutinaDescripcion)
    }
}
