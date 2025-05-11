package com.example.irongym.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.R
import com.example.irongym.entity.Desafio

class DesafioAdapter(
    private val onCheckedChange: ((Desafio, Boolean) -> Unit)? = null  // 🔥 Añadido callback opcional
) : ListAdapter<Desafio, DesafioAdapter.DesafioViewHolder>(DesafioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesafioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_desafio, parent, false)
        return DesafioViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesafioViewHolder, position: Int) {
        val desafio = getItem(position)
        holder.bind(desafio)
    }

    inner class DesafioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTituloDesafio: TextView = itemView.findViewById(R.id.tvTituloDesafio)
        private val tvDescripcionDesafio: TextView = itemView.findViewById(R.id.tvDescripcionDesafio)
        private val ivDesafioImagen: ImageView = itemView.findViewById(R.id.ivDesafioImagen)
        private val checkCompletado: CheckBox = itemView.findViewById(R.id.checkCompletado)

        fun bind(desafio: Desafio) {
            tvTituloDesafio.text = desafio.titulo
            tvDescripcionDesafio.text = desafio.descripcion

            if (!desafio.imagenUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(desafio.imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivDesafioImagen)
            } else {
                ivDesafioImagen.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // 🔥 Aquí actualizamos la UI y enviamos el cambio hacia afuera
            checkCompletado.setOnCheckedChangeListener(null)  // Evitar eventos basura al reciclar
            checkCompletado.isChecked = desafio.activo
            checkCompletado.setOnCheckedChangeListener { _, isChecked ->
                desafio.activo = isChecked
                desafio.cambiado = true  // 🔥 Marcamos que este desafío ha cambiado
                onCheckedChange?.invoke(desafio, isChecked)
            }

        }
    }

    class DesafioDiffCallback : DiffUtil.ItemCallback<Desafio>() {
        override fun areItemsTheSame(oldItem: Desafio, newItem: Desafio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Desafio, newItem: Desafio): Boolean {
            return oldItem == newItem
        }
    }
}
