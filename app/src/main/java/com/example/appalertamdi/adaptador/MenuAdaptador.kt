package com.example.appalertamdi.adaptador

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.appalertamdi.R
import com.example.appalertamdi.RegistrarActivity
import com.example.appalertamdi.config.Constantes
import com.example.appalertamdi.entidad.Categoria

class MenuAdaptador(private val dataSet: ArrayList<Categoria>) :
    RecyclerView.Adapter<MenuAdaptador.ViewHolder>() {

    class ViewHolder(val vista: View) : RecyclerView.ViewHolder(vista) {
        val nombreCategoria: TextView = vista.findViewById(R.id.txtNombre)
        val iconoCategoria: ImageView = vista.findViewById(R.id.imgCategoria)
        val layoutItem: View = vista.findViewById(R.id.layoutItem)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.categoria_grid_item, viewGroup, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]

        // Configurar nombre de categoría
        holder.nombreCategoria.text = item.nombre

        // Configurar color de fondo del ítem
        holder.layoutItem.setBackgroundColor(android.graphics.Color.parseColor(item.color))

        // Configurar icono basado en el nombre del icono en la entidad
        val iconoId = holder.itemView.context.resources.getIdentifier(
            item.icono, "drawable", holder.itemView.context.packageName
        )

        if (iconoId != 0) {
            holder.iconoCategoria.setImageResource(iconoId)
        } else {
            // Icono por defecto si no se encuentra
            holder.iconoCategoria.setImageResource(R.drawable.ic_report)
        }

        // Configurar clic en el ítem
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RegistrarActivity::class.java)
            intent.putExtra(Constantes.ID_CATEGORIA, item.id.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataSet.size

    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }
}