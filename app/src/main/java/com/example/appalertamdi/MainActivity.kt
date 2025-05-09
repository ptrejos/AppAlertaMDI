package com.example.appalertamdi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.entidad.Usuario
import com.example.appalertamdi.config.configuracion
import com.example.appalertamdi.databinding.ActivityMainBinding


//variables globales para el codigo y nombre de usuario
object GlobalData {
    var codigo = ""
    var nombre = ""
}

class MainActivity : AppCompatActivity() {

    private lateinit var loadingDialog: AlertDialog
    lateinit var apiWeb: ApiWeb;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiWeb = configuracion.obtenerConfiguracionRetrofit()

        val username = findViewById<EditText>(R.id.editTextUsername)
        val clave = findViewById<EditText>(R.id.editTextClave)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val user = username.text.toString()
            val pass = clave.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()){
                autenticarUsuario(user, pass)
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        setupLoadingDialog()
    }

     fun autenticarUsuario(user: String, pass: String) {
        //val callRespuesta = apiWeb.validaUsuario("validaLogin")
         val callRespuesta = apiWeb.validaUsuario("validalogin", user, pass)
         showLoadingDialog();
        callRespuesta.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                //Log.d("Mensaje_Error", "Error al consumir el servicio ${t.localizedMessage}", t)
                Toast.makeText(
                    this@MainActivity,
                    "Error al consumir el servicio : ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    val usuarios = response.body()
                    if (usuarios != null && usuarios.isNotEmpty()) {
                        val usuario = usuarios[0]
                        val rs = usuario.rs

                        if (rs == "1") {

                            GlobalData.codigo = usuario.codigo
                            GlobalData.nombre = usuario.nombre
                            menuPrincipal() // Ejecutar la función deseada

                        } else if (rs == "0") {
                            hideLoadingDialog()
                            Toast.makeText(
                                this@MainActivity,
                                "Usuario o clave incorrecta",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        hideLoadingDialog()
                        Toast.makeText(
                            this@MainActivity,
                            "No hay datos devuelos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    hideLoadingDialog()
                    //Log.d("Mensaje_Error", "Error de conexión ${response.code()} - ${response.message()}")
                        Toast.makeText(
                            this@MainActivity,
                            "Error de conexión ${response.code()} - ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        })
    }

    fun menuPrincipal() {
         val intent = Intent(this@MainActivity, PrincipalActivity::class.java)
         startActivity(intent)
         finish()
        hideLoadingDialog()
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




}