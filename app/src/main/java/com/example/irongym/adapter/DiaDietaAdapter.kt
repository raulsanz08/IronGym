package com.example.irongym.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.irongym.DetalleDiaDietaActivity
import com.example.irongym.R
import com.example.irongym.entity.DiaDieta

class DiaDietaAdapter(
    private val listaDias: List<DiaDieta>,
    private val checkedStates: MutableList<Boolean>
) : RecyclerView.Adapter<DiaDietaAdapter.DiaDietaViewHolder>() {

    class DiaDietaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloDiaDieta)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionDiaDieta)
        val ivImagen: ImageView = itemView.findViewById(R.id.ivDiaDieta)
        val check: CheckBox = itemView.findViewById(R.id.checkCompletado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaDietaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_dieta, parent, false)
        return DiaDietaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaDietaViewHolder, position: Int) {
        val dia = listaDias[position]
        holder.tvTitulo.text = dia.dia
        holder.tvDescripcion.text = "${dia.descripcion} kcal"

        Glide.with(holder.itemView.context)
            .load(dia.imagenUrl)
            .into(holder.ivImagen)

        holder.check.isChecked = checkedStates[position]

        holder.check.setOnCheckedChangeListener { _, isChecked ->
            checkedStates[position] = isChecked
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleDiaDietaActivity::class.java)
            intent.putExtra("DIA_DIETA_ID", dia.id)
            intent.putExtra("DIA_DIETA_NOMBRE", dia.dia)
            intent.putExtra("IMAGEN_URL", dia.imagenUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaDias.size
}
