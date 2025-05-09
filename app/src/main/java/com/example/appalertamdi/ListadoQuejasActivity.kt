package com.example.appalertamdi

import android.app.DownloadManager.Query
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appalertamdi.adaptador.ListaQuejasAdaptador
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.config.configuracion
import com.example.appalertamdi.databinding.ListaQuejasBinding

import com.example.appalertamdi.entidad.QueryListadoQuejas
import com.example.appalertamdi.entidad.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListadoQuejasActivity: AppCompatActivity() {

    private lateinit var loadingDialog: AlertDialog
    lateinit var binding: ListaQuejasBinding
    lateinit var apiWeb: ApiWeb;
    var listadoQuejas = ArrayList<QueryListadoQuejas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaQuejasBinding.inflate(layoutInflater)
        setContentView(R.layout.lista_quejas)
        binding = ListaQuejasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiWeb = configuracion.obtenerConfiguracionRetrofit()
        setupLoadingDialog()

        listamenuGet();
        binding.fabNuevaQueja.visibility = View.INVISIBLE;


    }

    fun listamenuGet() {

    showLoadingDialog()
        val respuesta = apiWeb.listaQuejasXUsuario("listaMisQuejas",1)

        respuesta.enqueue(object : Callback<ArrayList<QueryListadoQuejas>> {
            // Cuando falla el consumo
            override fun onFailure(call: Call<ArrayList<QueryListadoQuejas>>, t: Throwable) {
                hideLoadingDialog()
                Log.d("Mensaje_Error", "Error al consumir el servicio ${t.localizedMessage}", t)
            }

            // Cuando consume el servicio correctamente
            override fun onResponse(
                call: Call<ArrayList<QueryListadoQuejas>>,
                response: Response<ArrayList<QueryListadoQuejas>>
            ) {
                if (response.isSuccessful) {

                    for (quejas in response.body()!!){
                       // Log.d("Mensaje",  "${categoria.id} | ${categoria.nombre} | ${categoria.icono}")
                        listadoQuejas.add(quejas)
                    }

                    binding.vistaListaQuejas.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                    }

                    var miAdaptador = ListaQuejasAdaptador(listadoQuejas)
                    binding.vistaListaQuejas.adapter = miAdaptador
                    hideLoadingDialog()
                }

            }
        })

    }

    private fun showLoadingDialog() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    private fun setupLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false) // Evita que se cierre tocando fuera

        loadingDialog = builder.create()
    }

    private fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
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

