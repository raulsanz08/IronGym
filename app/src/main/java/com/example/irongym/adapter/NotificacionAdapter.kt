package com.example.irongym.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.irongym.R
import com.example.irongym.entity.Notificacion

class NotificacionAdapter(
    private var notificaciones: MutableList<Notificacion> = mutableListOf(),
    private val onCheckedChange: ((Notificacion, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder>() {

    inner class NotificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(notificacion: Notificacion) {
            descripcion.text = notificacion.descripcion
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = notificacion.activo

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                notificacion.activo = isChecked
                onCheckedChange?.invoke(notificacion, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        holder.bind(notificaciones[position])
    }

    override fun getItemCount(): Int = notificaciones.size

    fun updateNotificaciones(newNotificaciones: List<Notificacion>) {
        notificaciones = newNotificaciones.toMutableList()
        notifyDataSetChanged()
    }

    fun getNotificaciones(): List<Notificacion> {
        return notificaciones
    }
}
