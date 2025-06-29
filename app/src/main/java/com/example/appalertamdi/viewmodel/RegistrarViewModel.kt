// src/main/java/com/example/appalertamdi/viewmodel/RegistrarViewModel.kt
package com.example.appalertamdi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.entidad.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONArray


// Se inyectarán ApiWeb y un cliente de ubicación si la lógica de ubicación
// ApiWeb.
class RegistrarViewModel(private val apiWeb: ApiWeb) : ViewModel() {

    // LiveData para observar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para las categorías
    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> = _categorias

    // LiveData para mensajes de error/éxito (usar con cuidado, puede ser un Event)
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    // LiveData para el resultado del registro (ej. éxito/fallo)
    private val _registrationResult = MutableLiveData<Boolean>()
    val registrationResult: LiveData<Boolean> = _registrationResult

    // Variables internas que mantendrán el estado de los campos del formulario
    var selectedCategoryId: String = ""
    var description: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var selectedFileExists: Boolean = false // Indica si hay una foto tomada

    // Lógica para cargar categorías
   fun loadCategorias() {
        _isLoading.value = true // Indica que la carga ha comenzado

       // Mueve la llamada a la API de tu Activity aquí
        apiWeb.listarMenu("listaMenu").enqueue(object : Callback<ArrayList<Categoria>> {
            //apiWeb.listarMenu().enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ArrayList<Categoria>>, t: Throwable) {
               _isLoading.value = false // Carga terminada
                _message.value = "Error al cargar categorías: ${t.message}"
                _categorias.value = emptyList() // O un estado de error específico
            }

            override fun onResponse(
                call: Call<ArrayList<Categoria>>,
                response: Response<ArrayList<Categoria>>
            ) {
                _isLoading.value = false // Carga terminada
                if (response.isSuccessful && response.body() != null) {
                   _categorias.value = response.body() // Publica las categorías
                } else {
                    _message.value = "Error al cargar categorías: Respuesta no exitosa"
                    _categorias.value = emptyList()
                }
            }
       })
   }



    // Lógica para validar el formulario (sin tocar la UI)
    fun validateForm(): Boolean {
        // La validación ahora solo devuelve un booleano y no interactúa con Views
        // La Activity se encargará de mostrar los errores basándose en el resultado
        var isValid = true

        if (selectedCategoryId.isEmpty()) {
            _message.value = "Seleccione una categoría" // Mensaje para la UI
            isValid = false
        }

        if (description.trim().isEmpty()) {
            _message.value = "Ingrese una descripción"
            isValid = false
        }

        if (latitud.isEmpty() || longitud.isEmpty()) {
            _message.value = "No se pudo obtener la ubicación"
            isValid = false
        }

        if (!selectedFileExists) {
            _message.value = "Debe tomar una foto de la incidencia"
            isValid = false
        }
        return isValid
    }

    // Lógica para registrar la incidencia
    // Recibe los RequestBody porque el ViewModel no debe saber de File o Uri directamente,
    // es responsabilidad de la Activity preparar estos objetos.
    fun registerIncidencia(
        categoriaId: RequestBody,
        usuarioId: RequestBody,
        descripcion: RequestBody,
        latitud: RequestBody,
        longitud: RequestBody,
        imagenPart: MultipartBody.Part
    ) {
        _isLoading.value = true

        apiWeb.registrarIncidencia_2(
            categoriaId, usuarioId, descripcion, latitud, longitud, imagenPart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    //val bodyString = response.body()?.string()
                    val responseBody = response.body()
                    val bodyString = responseBody?.string()
                    try {
                        val jsonObject = JSONObject(bodyString)
                        val rs = jsonObject.getString("rs")
                        if (rs == "TRUE") {
                            _message.value = "¡Incidencia registrada correctamente!"
                            _registrationResult.value = true // Indica éxito
                        } else {
                            _message.value = "Error al registrar: La respuesta del servidor no fue exitosa"
                            _registrationResult.value = false
                        }
                    } catch (e: Exception) {
                        _message.value = "Error al procesar la respuesta del servidor"
                        _registrationResult.value = false
                    }
                } else {
                    _message.value = "Error ${response.code()}: No se pudo registrar la incidencia"
                    _registrationResult.value = false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Error de conexión: Verifica tu conexión a internet"
                _registrationResult.value = false
            }
        })
    }

    // inyectando un "LocationProvider" mockeable.
}