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
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

//variables globales para el codigo y nombre de usuario
object GlobalData {
    var codigo = ""
    var nombre = ""
}

class MainActivity : AppCompatActivity() {

    private lateinit var loadingDialog: AlertDialog
    private lateinit var apiWeb: ApiWeb // Cambiado a private y sin punto y coma

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiWeb = configuracion.obtenerConfiguracionRetrofit()

        val username = findViewById<EditText>(R.id.editTextUsername)
        val clave = findViewById<EditText>(R.id.editTextClave)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val user = username.text.toString().trim() // Agregado trim()
            val pass = clave.text.toString().trim() // Agregado trim()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                autenticarUsuario(user, pass)
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        setupLoadingDialog()
    }

    private fun autenticarUsuario(user: String, pass: String) {
        val callRespuesta = apiWeb.validaUsuario("validalogin", user, pass)
        showLoadingDialog()

        callRespuesta.enqueue(object : Callback<List<Usuario>> {
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                hideLoadingDialog()

                val errorMessage = when (t) {
                    is SocketTimeoutException -> "Tiempo de espera agotado. El servidor tardó demasiado en responder"
                    is ConnectException -> "No se pudo conectar al servidor"
                    is IOException -> "Error de conexión. Verifica tu internet"
                    is HttpException -> "Error del servidor: ${t.code()}"
                    else -> "Error inesperado: ${t.localizedMessage}"
                }

                Log.e("AuthError", "Error al autenticar: ${t.javaClass.simpleName}", t)
                Toast.makeText(
                    this@MainActivity,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                hideLoadingDialog() // Siempre ocultar el dialog al recibir respuesta

                if (response.isSuccessful) {
                    val usuarios = response.body()
                    if (!usuarios.isNullOrEmpty()) {
                        val usuario = usuarios[0]
                        val rs = usuario.rs

                        when (rs) {
                            "1" -> {
                                // Login exitoso
                                GlobalData.codigo = usuario.codigo
                                GlobalData.nombre = usuario.nombre
                                Log.d("Login", "Usuario autenticado: ${usuario.nombre}")
                                menuPrincipal()
                            }
                            "0" -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Usuario o contraseña incorrecta",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Log.w("Login", "Estado de respuesta desconocido: $rs")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error en la autenticación. Intenta nuevamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No hay datos devueltos", // Corregido typo
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("Login", "Error HTTP: ${response.code()} - ${response.message()}")

                    val errorMessage = when (response.code()) {
                        401 -> "Credenciales inválidas"
                        404 -> "Servicio no encontrado"
                        500 -> "Error interno del servidor"
                        else -> "Error de conexión: ${response.code()}"
                    }

                    Toast.makeText(
                        this@MainActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun menuPrincipal() {
        val intent = Intent(this@MainActivity, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
        // Removido hideLoadingDialog() porque ya se llama en onResponse
    }

    private fun showLoadingDialog() {
        if (::loadingDialog.isInitialized && !loadingDialog.isShowing) {
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
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}