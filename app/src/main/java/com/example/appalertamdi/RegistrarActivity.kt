package com.example.appalertamdi

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.config.Constantes
import com.example.appalertamdi.config.configuracion
import com.example.appalertamdi.databinding.IncidenteRegistrarBinding
import com.example.appalertamdi.entidad.Categoria
// Importaciones para ViewModel y LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.appalertamdi.viewmodel.RegistrarViewModel
import com.example.appalertamdi.viewmodel.RegistrarViewModelFactory
// Fin de importaciones para ViewModel y LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
// Comentado: CircularProgressIndicator no se usa directamente en esta lógica refactorizada
// import com.google.android.material.progressindicator.CircularProgressIndicator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog

class RegistrarActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val LOCATION_PERMISSION_REQUEST = 102
        private var selectedFile: File? = null
        // Comentado: Estas variables ahora se gestionan en el ViewModel. Se dejan aquí solo como referencia
        // pero la Activity usará las del ViewModel.
        // private var id_Categoria: String = ""
        // private var vl_latitud: String = ""
        // private var vl_longitud: String = ""
    }

    lateinit var binding: IncidenteRegistrarBinding
    // Comentado: apiWeb ya no se usa directamente en la Activity, el ViewModel la inyecta
    // lateinit var apiWeb: ApiWeb
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // Comentado: Las categorías se gestionan en el ViewModel
    // private var categorias = ArrayList<Categoria>()
    // Comentado: isLoading se gestiona en el ViewModel
    // private var isLoading = false
    private lateinit var loadingDialog: AlertDialog

    // Nuevo: Instancia del ViewModel
    private lateinit var registrarViewModel: RegistrarViewModel

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IncidenteRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar dependencias
        // Comentado: apiWeb ya no se inicializa aquí para la Activity
        // apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Nuevo: Inicialización del ViewModel
        val apiWebInstance = configuracion.obtenerConfiguracionRetrofit() // Obtén ApiWeb para el factory
        val factory = RegistrarViewModelFactory(apiWebInstance)
        registrarViewModel = ViewModelProvider(this, factory).get(RegistrarViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar la barra de herramientas
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Obtener ID de categoría si fue pasado y asignarlo al ViewModel
        // La Activity mantiene esta línea para pasar el valor inicial al ViewModel.
        registrarViewModel.selectedCategoryId = intent.getStringExtra(Constantes.ID_CATEGORIA) ?: ""

        // Configurar componentes
        configurarCategoriasDropdown()
        configurarBotonCapturarImagen()
        configurarBotonGrabar()

        // Iniciar la obtención de ubicación automáticamente
        verificarPermisosUbicacion()

        // Mostrar mensaje tutorial al usuario (si es necesario)
        //Toast.makeText(this, "Complete la información y tome una foto para registrar la incidencia", Toast.LENGTH_LONG).show()

        // Nuevo: Inicialización del diálogo de carga ANTES de su posible uso.
        setupLoadingDialog()

        // Nuevo: Observar los LiveData del ViewModel
        observeViewModel()

        // Nuevo: Cargar categorías al inicio desde el ViewModel
        registrarViewModel.loadCategorias()
    }

    private fun configurarCategoriasDropdown() {
        // La lógica de carga de categorías ahora está en el ViewModel.
        // Este método solo configura el AutoCompleteTextView y su listener.

        binding.actvCategoria.setOnItemClickListener { parent, _, position, _ ->
            val nombreSeleccionado = parent.getItemAtPosition(position) as String
            // Accede a la lista de categorías del ViewModel para encontrar el ID
            val categoriaSeleccionada = registrarViewModel.categorias.value?.find { it.nombre == nombreSeleccionado }
            categoriaSeleccionada?.let {
                // Actualiza la variable del ViewModel con la categoría seleccionada
                registrarViewModel.selectedCategoryId = it.id.toString()
                Log.d("Categoria", "Seleccionada: ${it.nombre}, ID: ${it.id}")
            }
        }
    }

    private fun configurarBotonCapturarImagen() {
        binding.btnCapturar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun configurarBotonGrabar() {
        binding.btnGrabar.setOnClickListener {
            // Antes de validar, actualizar el estado del ViewModel con los datos de la UI
            registrarViewModel.description = binding.etDescripcion.text.toString().trim()
            registrarViewModel.selectedFileExists = (selectedFile != null && selectedFile!!.exists())

            // Llamar al método de validación del ViewModel
            if (registrarViewModel.validateForm()) {
                // Si la validación en el ViewModel pasa, procede a intentar registrar.
                // showLoadingDialog() // El observador de isLoading en el ViewModel lo gestionará

                selectedFile?.let { file ->
                    // Preparar los RequestBody para el ViewModel
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagenPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
                    val categoriaId = registrarViewModel.selectedCategoryId.toRequestBody("text/plain".toMediaTypeOrNull())
                    val usuarioId = GlobalData.codigo.toRequestBody("text/plain".toMediaTypeOrNull())
                    val descripcion = registrarViewModel.description.toRequestBody("text/plain".toMediaTypeOrNull())
                    val latitud = registrarViewModel.latitud.toRequestBody("text/plain".toMediaTypeOrNull())
                    val longitud = registrarViewModel.longitud.toRequestBody("text/plain".toMediaTypeOrNull())

                    // Llamar al método de registro del ViewModel
                    registrarViewModel.registerIncidencia(
                        categoriaId, usuarioId, descripcion, latitud, longitud, imagenPart
                    )
                } ?: run {
                    // Fallback si selectedFile es nulo después de la validación (esto no debería pasar si validateForm() es robusto)
                    Toast.makeText(this, "Error interno: Archivo de imagen no disponible.", Toast.LENGTH_SHORT).show()
                    // hideLoadingDialog() // Ya el observador de isLoading lo gestionará
                }
            } else {
                // La validación falló en el ViewModel, el mensaje ya se habrá publicado
                // en el LiveData 'message' y el observador lo mostrará junto con los errores de TIL.
            }
        }
    }

    private fun verificarPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            obtenerUbicacion()
        }
    }

    private var currentPhotoPath: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.txtNoImagen.visibility = View.GONE

            currentPhotoPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    selectedFile = file
                    val bitmap = BitmapFactory.decodeFile(path)
                    bitmap?.let {
                        binding.imgFoto.setImageBitmap(it)
                        // Asegura que el ViewModel sepa que hay una imagen.
                        registrarViewModel.selectedFileExists = true
                        return
                    }
                }
            }

            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                binding.imgFoto.setImageBitmap(it)
                try {
                    selectedFile = bitmapToFile(it, this)
                    // Asegura que el ViewModel sepa que hay una imagen.
                    registrarViewModel.selectedFileExists = true
                } catch (e: Exception) {
                    Log.e("Foto", "Error al guardar miniatura: ${e.message}")
                    Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                    // En caso de error, el ViewModel debe saber que no hay imagen válida.
                    registrarViewModel.selectedFileExists = false
                }
            } ?: run {
                // Si no se obtuvo ninguna imagen, el ViewModel debe saberlo.
                registrarViewModel.selectedFileExists = false
            }
        } else {
            // Si la cámara fue cancelada o falló
            registrarViewModel.selectedFileExists = (selectedFile != null && selectedFile!!.exists())
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File.createTempFile("upload_image", ".jpg", context.cacheDir)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        return file
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("Foto", "Error al crear archivo: ${ex.message}")
                    Toast.makeText(this, "Error al preparar la cámara", Toast.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            } ?: run {
                Toast.makeText(this, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


/* Comentado: La lógica de validación se movió al ViewModel.
 * Mantener este bloque comentado como referencia si se desea revertir,
 * pero no debe estar activo para que el ViewModel funcione.
 */
/*
private fun validarFormulario(): Boolean {
    var esValido = true

    // Validar categoría
    if (id_Categoria.isEmpty() || id_Categoria == "null") {
        binding.tilCategoria.error = "Seleccione una categoría"
        esValido = false
    } else {
        binding.tilCategoria.error = null
    }

    // Validar descripción
    val descripcion = binding.etDescripcion.text.toString().trim()
    if (descripcion.isEmpty()) {
        binding.tilDescripcion.error = "Ingrese una descripción"
        esValido = false
    } else {
        binding.tilDescripcion.error = null
    }

    // Validar ubicación
    if (vl_latitud.isEmpty() || vl_longitud.isEmpty()) {
        binding.tilUbicacion.error = "No se pudo obtener la ubicación"
        Toast.makeText(this, "No se pudo obtener la ubicación. Verifica los permisos o el GPS", Toast.LENGTH_LONG).show()
        esValido = false
    } else {
        binding.tilUbicacion.error = null
    }

    // Validar imagen
    if (selectedFile == null || !selectedFile!!.exists()) {
        Toast.makeText(this, "Debe tomar una foto de la incidencia", Toast.LENGTH_SHORT).show()
        esValido = false
    }

    return esValido
}
*/

/* Comentado: La lógica de registro de incidencia se movió al ViewModel.
 Mantener este bloque comentado como referencia si se desea revertir,
 pero no debe estar activo para que el ViewModel funcione.*/

//fun registrarIncidencia(d: File) {
//    showLoadingDialog();
//    if (!d.exists()) {
//        Toast.makeText(applicationContext, "No se puede obtener la foto", Toast.LENGTH_LONG).show()
//        mostrarCargando(false)
//        hideLoadingDialog()
//        return
//    }
//
//    if (isLoading) {
//        // Evitar múltiples envíos
//        hideLoadingDialog()
//        return
//    }
//
//    isLoading = true
//
//    // Deshabilitar botón para evitar múltiples envíos
//    binding.btnGrabar.isEnabled = false
//
//    val descrip = binding.etDescripcion.text.toString().trim()
//
//    val requestFile = d.asRequestBody("image/*".toMediaTypeOrNull())
//    val imagenPart = MultipartBody.Part.createFormData("foto", d.name, requestFile)
//    val categoriaId = id_Categoria.toRequestBody("text/plain".toMediaTypeOrNull())
//    val usuarioId = GlobalData.codigo.toRequestBody("text/plain".toMediaTypeOrNull())
//    val descripcion = descrip.toRequestBody("text/plain".toMediaTypeOrNull())
//    val latitud = vl_latitud.toRequestBody("text/plain".toMediaTypeOrNull())
//    val longitud = vl_longitud.toRequestBody("text/plain".toMediaTypeOrNull())
//
//    var callRespuesta: Call<ResponseBody> = apiWeb.registrarIncidencia_2(
//        categoriaId, usuarioId, descripcion, latitud, longitud, imagenPart
//    )
//
//    callRespuesta.enqueue(object : Callback<ResponseBody> {
//        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//            isLoading = false
//            mostrarCargando(false)
//            binding.btnGrabar.isEnabled = true
//
//            if (response.isSuccessful) {
//                val bodyString = response.body()?.string()
//                Log.d("API_RESPONSE", "Respuesta cruda: $bodyString")
//
//                try {
//                    val jsonObject = JSONObject(bodyString)
//                    val rs = jsonObject.getString("rs")
//
//                    if (rs == "TRUE") {
//                        Toast.makeText(applicationContext, "¡Incidencia registrada correctamente!", Toast.LENGTH_LONG).show()
//                        val intent = Intent(applicationContext, PrincipalActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(intent)
//                        finish()
//                        hideLoadingDialog()
//                    } else {
//                        Toast.makeText(applicationContext, "Error al registrar: La respuesta del servidor no fue exitosa", Toast.LENGTH_LONG).show()
//                        hideLoadingDialog()
//                    }
//                } catch (e: Exception) {
//                    hideLoadingDialog()
//                    Log.e("API_RESPONSE", "Error al parsear JSON", e)
//                    Toast.makeText(applicationContext, "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show()
//                }
//            } else {
//                val errorBody = response.errorBody()?.string()
//                Log.e("API_ERROR", "Error: ${response.code()}, $errorBody")
//                hideLoadingDialog()
//                Toast.makeText(
//                    applicationContext,
//                    "Error ${response.code()}: No se pudo registrar la incidencia",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//
//        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//            isLoading = false
//            mostrarCargando(false)
//            binding.btnGrabar.isEnabled = true
//
//            t.printStackTrace()
//            Log.e("API_FAILURE", "Error: ${t.message}", t)
//            Toast.makeText(
//                applicationContext,
//                "Error de conexión: Verifica tu conexión a internet",
//                Toast.LENGTH_LONG
//            ).show()
//
//            hideLoadingDialog()
//        }
//    })
//}



fun mostrarImagenCompleta(view: View?) {
    if (selectedFile == null || !selectedFile!!.exists()) {
        Toast.makeText(this, "Imagen no cargada aún", Toast.LENGTH_SHORT).show()
        return
    }

    val dialog: Dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    dialog.setContentView(R.layout.dialog_imagen_completa)

    val imageView: ImageView = dialog.findViewById(R.id.fullscreenImage)
    imageView.setImageURI(Uri.fromFile(selectedFile))

    imageView.setOnClickListener { dialog.dismiss() }
    dialog.show()
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun obtenerUbicacion() {
    Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()

    binding.etUbicacion.setText("Obteniendo ubicación...")

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Actualiza el ViewModel con la ubicación
                registrarViewModel.latitud = location.latitude.toString()
                registrarViewModel.longitud = location.longitude.toString()

                binding.etUbicacion.setText("Lat: ${location.latitude}, Long: ${location.longitude}")
                Log.d("Ubicacion", "Ubicación obtenida - Lat: ${location.latitude}, Long: ${location.longitude}")
            } else {
                // Limpia la ubicación en el ViewModel si no se pudo obtener
                registrarViewModel.latitud = ""
                registrarViewModel.longitud = ""

                binding.etUbicacion.setText("No se pudo obtener ubicación")
                Toast.makeText(this, "Ubicación no disponible. Activa el GPS", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            // Limpia la ubicación en el ViewModel en caso de error
            registrarViewModel.latitud = ""
            registrarViewModel.longitud = ""

            binding.etUbicacion.setText("Error al obtener ubicación")
            Log.e("Ubicacion", "Error al obtener ubicación: ${e.message}")
            Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

// Nuevo: Método para observar los LiveData del ViewModel
private fun observeViewModel() {
    registrarViewModel.isLoading.observe(this) { isLoading ->
        // Controla la visibilidad del diálogo de carga y la habilitación del botón
        if (isLoading) {
            showLoadingDialog()
        } else {
            hideLoadingDialog()
        }
        binding.btnGrabar.isEnabled = !isLoading
    }

    registrarViewModel.categorias.observe(this) { categoriasList ->
        if (categoriasList != null) {
            val nombresCategoria = categoriasList.map { it.nombre }
            val adapter = ArrayAdapter(
                this@RegistrarActivity,
                android.R.layout.simple_dropdown_item_1line,
                nombresCategoria
            )
            binding.actvCategoria.setAdapter(adapter)

            // Si se recibió un ID de categoría inicial al crear la Activity, seleccionarlo
            // Esta lógica ahora usa la propiedad del ViewModel
            if (registrarViewModel.selectedCategoryId.isNotEmpty() && registrarViewModel.selectedCategoryId != "null") {
                val categoria = categoriasList.find { it.id.toString() == registrarViewModel.selectedCategoryId }
                categoria?.let {
                    binding.actvCategoria.setText(it.nombre, false)
                }
            }
            // Comentado: 'this.categorias' de la Activity ya no es la fuente principal de datos.
            // this.categorias = ArrayList(categoriasList)
        }
    }

    // Observador de mensajes (ej. de validación o errores del servidor)
    registrarViewModel.message.observe(this) { message ->
        if (message.isNotEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            // Lógica para mostrar errores de validación en los TextInputLayout
            // Si el mensaje contiene palabras clave, se asigna el error.
            // De lo contrario, se limpia el error.
            binding.tilCategoria.error = if (message.contains("categoría", ignoreCase = true)) message else null
            binding.tilDescripcion.error = if (message.contains("descripción", ignoreCase = true)) message else null
            binding.tilUbicacion.error = if (message.contains("ubicación", ignoreCase = true)) message else null
            // El error de la foto es un Toast, no necesita TextInputLayout.
        }
    }

    // Observador del resultado final del registro
    registrarViewModel.registrationResult.observe(this) { isSuccess ->
        if (isSuccess) {
            val intent = Intent(applicationContext, PrincipalActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        } else {
            // Si el registro falla, el mensaje ya se habrá mostrado por el observador de 'message'.
            // Aquí no se requiere acción adicional de la UI, solo se mantiene el estado del diálogo.
        }
    }
}


// Comentado: El estado de 'isLoading' ahora lo gestiona el ViewModel y el observador.
// Este método solo se encarga de la UI visual.
private fun mostrarCargando(mostrar: Boolean) {
    // El control del diálogo de carga ya lo hace 'observeViewModel()'
    // if (mostrar) {
    //    showLoadingDialog()
    // } else {
    //    hideLoadingDialog()
    // }
    binding.btnGrabar.isEnabled = !mostrar
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        LOCATION_PERMISSION_REQUEST -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Se necesita permiso de ubicación para reportar incidencias", Toast.LENGTH_LONG).show()
            }
        }
        CAMERA_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
            }
        }
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

    loadingDialog = builder.create() // ¡Esta línea es CRÍTICA para inicializar!
}

private fun hideLoadingDialog() {
    if (loadingDialog.isShowing) {
        loadingDialog.dismiss()
    }
}
}