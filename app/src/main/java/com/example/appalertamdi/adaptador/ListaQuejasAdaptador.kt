package com.example.appalertamdi.adaptador

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appalertamdi.ConsultarQuejaActivity
import com.example.appalertamdi.R
import com.example.appalertamdi.config.Constantes
import com.example.appalertamdi.entidad.QueryListadoQuejas
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Locale

class ListaQuejasAdaptador(private val listaQuejas: List<QueryListadoQuejas>) :
    RecyclerView.Adapter<ListaQuejasAdaptador.ViewHolder>() {

    // Listener para manejar clicks en los elementos
    private var onItemClickListener: ((QueryListadoQuejas) -> Unit)? = null


    // Método para establecer el listener desde la actividad
    fun setOnItemClickListener(listener: (QueryListadoQuejas) -> Unit) {
        onItemClickListener = listener
    }

    // Clase ViewHolder para los elementos de la lista
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a las vistas en el layout
        val cardView: MaterialCardView = itemView as MaterialCardView
        val indicadorEstado: View = itemView.findViewById(R.id.estadoIndicador)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val btnVerDetalles: MaterialButton = itemView.findViewById(R.id.btnVerDetalles)
        val btnSeguimiento: MaterialButton = itemView.findViewById(R.id.btnSeguimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quejas_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val queja = listaQuejas[position]
        val context = holder.itemView.context

        // Configurar datos básicos
        holder.txtTitulo.text = queja.descripcion_categoria
        holder.txtDescripcion.text = queja.queja_descripcion

        // Formatear fecha para mejor legibilidad
        try {
            val fechaOriginal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(queja.fecha_hora)
            val fechaFormateada = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                .format(fechaOriginal!!)
            holder.txtFecha.text = fechaFormateada
        } catch (e: Exception) {
            // Si hay error en el formato, mostrar fecha original
            holder.txtFecha.text = queja.fecha_hora
        }

        // Configurar estado
        holder.txtEstado.text = queja.estado_atencion

        // Colores según el estado o usar el color del modelo si está disponible
        val colorEstado = when {
            queja.estado_atencion.contains("pendiente", ignoreCase = true) -> {
                R.color.color_secundario
            }
            queja.estado_atencion.contains("proceso", ignoreCase = true) -> {
                R.color.color_primario
            }
            queja.estado_atencion.contains("resuelt", ignoreCase = true) ||
                    queja.estado_atencion.contains("atendid", ignoreCase = true) -> {
                R.color.color_acento
            }
            else -> R.color.color_terciario
        }

        // Aplicar colores - usar el color propio del modelo si está disponible
        val color = if (queja.color.isNotEmpty()) {
            try {
                android.graphics.Color.parseColor(queja.color)
            } catch (e: Exception) {
                ContextCompat.getColor(context, colorEstado)
            }
        } else {
            ContextCompat.getColor(context, colorEstado)
        }

        // Aplicar color al indicador de estado
        holder.indicadorEstado.backgroundTintList = ColorStateList.valueOf(color)

        // Configurar botones
        holder.btnVerDetalles.setOnClickListener {
           // onItemClickListener?.invoke(queja)
            val intent = Intent(holder.itemView.context, ConsultarQuejaActivity::class.java)
            intent.putExtra(Constantes.VL_LONGITUD, queja.longitud.toString())
            intent.putExtra(Constantes.VL_LATITUD, queja.latitud.toString())
            intent.putExtra(Constantes.CQUEJA_DESCRIPCION, queja.queja_descripcion.toString())
            intent.putExtra(Constantes.CRUTA_FOTO, queja.foto.toString())
            intent.putExtra(Constantes.CESTADO, queja.estado_atencion.toString())
            intent.putExtra(Constantes.CCATEGORIA, queja.descripcion_categoria.toString())
            intent.putExtra(Constantes.CFECHA_HORA, queja.fecha_hora.toString())
            holder.itemView.context.startActivity(intent)
        }

        holder.btnSeguimiento.setOnClickListener {
            // Implementar funcionalidad específica de seguimiento si es necesario
            // Por ahora, mismo comportamiento que ver detalles
            onItemClickListener?.invoke(queja)
        }
    }

    override fun getItemCount(): Int = listaQuejas.size

}