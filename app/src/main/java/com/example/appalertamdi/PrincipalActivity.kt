package com.example.appalertamdi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.databinding.MenuPrincipalBinding
import com.example.appalertamdi.entidad.Categoria
import com.example.appalertamdi.adaptador.MenuAdaptador
import com.example.appalertamdi.config.configuracion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PrincipalActivity : AppCompatActivity() {

    lateinit var binding: MenuPrincipalBinding
    lateinit var apiWeb: ApiWeb
    var listaCategoria = ArrayList<Categoria>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Configurar el mensaje de bienvenida
        binding.textBienvenida.text = "Bienvenido: ${GlobalData.nombre}"

        // Configurar el RecyclerView como un grid de 2 columnas
        binding.vistaLista.layoutManager = GridLayoutManager(this, 2)

        // Configurar listener para el botón "Reportar Incidencia"
        binding.btnQuejas.setOnClickListener {
            // Limpiar la lista y cargar las categorías
            listaCategoria.clear()
            listamenuGet()
        }

        // Configurar listener para el botón flotante
        binding.fabNuevaQueja.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
        }

        // Configurar listener para el botón "Mis Incidencias"
        binding.btnMisQuejas.setOnClickListener {
            val intent = Intent(this, ListadoQuejasActivity::class.java)
            startActivity(intent)
        }

        // Configurar listener para la barra de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Ya estamos en Home, no hacer nada
                    true
                }
                R.id.navigation_profile -> {
                    // Implementar navegación al perfil del usuario
                    Toast.makeText(this, "Perfil de Usuario", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_help -> {
                    // Implementar navegación a ayuda/tutorial
                    Toast.makeText(this, "Tutorial de la aplicación", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Cargar las categorías al iniciar la actividad
        listamenuGet()
    }

    fun listamenuGet() {
        // Mostrar indicador de carga
        Toast.makeText(this, "Cargando categorías...", Toast.LENGTH_SHORT).show()

        val respuesta = apiWeb.listarMenu("listaMenu")

        respuesta.enqueue(object : Callback<ArrayList<Categoria>> {
            // Cuando falla el consumo
            override fun onFailure(call: Call<ArrayList<Categoria>>, t: Throwable) {
                Log.d("Mensaje_Error", "Error al consumir el servicio ${t.localizedMessage}", t)
                // Mostrar mensaje de error al usuario
                Toast.makeText(
                    this@PrincipalActivity,
                    "Error al cargar categorías: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Cuando consume el servicio correctamente
            override fun onResponse(
                call: Call<ArrayList<Categoria>>,
                response: Response<ArrayList<Categoria>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // Limpiar la lista antes de agregar nuevos elementos
                    listaCategoria.clear()

                    // Agregar las categorías recibidas a la lista
                    response.body()?.let { categorias ->
                        for (categoria in categorias) {
                            Log.d("Mensaje", "${categoria.id} | ${categoria.nombre} | ${categoria.icono}")
                            listaCategoria.add(categoria)
                        }
                    }

                    // Usar GridLayoutManager para mostrar en cuadrícula de 2 columnas
                    binding.vistaLista.layoutManager = GridLayoutManager(applicationContext, 2)
                    val miAdaptador = MenuAdaptador(listaCategoria)
                    binding.vistaLista.adapter = miAdaptador
                } else {
                    // Mostrar mensaje de error si la respuesta no es exitosa
                    Toast.makeText(
                        this@PrincipalActivity,
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    @Override
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus() // Opcional: Quita el foco del EditText
        }
        return super.dispatchTouchEvent(event)
    }

}