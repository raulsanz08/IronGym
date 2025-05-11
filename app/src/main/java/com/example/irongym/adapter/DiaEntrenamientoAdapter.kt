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
import com.example.irongym.DetalleEjercicioActivity
import com.example.irongym.R
import com.example.irongym.entity.DiaEntrenamiento

class DiaEntrenamientoAdapter(
    private val dias: List<DiaEntrenamiento>
) : RecyclerView.Adapter<DiaEntrenamientoAdapter.DiaViewHolder>() {

    // Mapa temporal para almacenar los estados de los checkbox
    private val estadosCheckBox = mutableMapOf<Int, Boolean>()

    inner class DiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDia: TextView = itemView.findViewById(R.id.tvDia)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionDia)
        val ivDia: ImageView = itemView.findViewById(R.id.ivImagenDia)
        val checkCompletado: CheckBox = itemView.findViewById(R.id.checkCompletado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_entrenamiento, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val dia = dias[position]
        holder.tvDia.text = dia.dia
        holder.tvDescripcion.text = dia.descripcion

        Glide.with(holder.itemView.context)
            .load(dia.imagenUrl)
            .into(holder.ivDia)

        // Asignar estado actual del checkbox
        holder.checkCompletado.isChecked = estadosCheckBox[dia.id] ?: false

        // Actualizar el estado cuando se marque o desmarque
        holder.checkCompletado.setOnCheckedChangeListener { _, isChecked ->
            estadosCheckBox[dia.id] = isChecked
        }

        // Clic en el item para abrir detalle
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleEjercicioActivity::class.java)
            intent.putExtra("DIA_ID", dia.id)
            intent.putExtra("DIA_NOMBRE", dia.dia)
            intent.putExtra("IMAGEN_URL", dia.imagenUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dias.size

    // Exponer los estados para que DetalleRutinaActivity pueda guardarlos
    fun obtenerEstadosCheckBox(): Map<Int, Boolean> {
        return estadosCheckBox
    }

    // Método para cargar los estados guardados
    fun cargarEstadosGuardados(estados: Map<Int, Boolean>) {
        estadosCheckBox.clear()
        estadosCheckBox.putAll(estados)
        notifyDataSetChanged()
    }
}
