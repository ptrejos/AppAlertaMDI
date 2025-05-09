package com.example.appalertamdi

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.appalertamdi.config.ApiWeb
import com.example.appalertamdi.config.Constantes
import com.example.appalertamdi.config.configuracion
import com.example.appalertamdi.databinding.ConsultaQuejaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File

class ConsultarQuejaActivity: AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private var vl_longitud: Double = 0.0
        private var vl_latitud: Double = 0.0
        private var vl_quedadescripcion: String = ""
        private var vl_ruta_foto: String = ""
        private var vl_estado: String = ""
        private var vl_categoria: String = ""
        private var vl_fecha_hora: String = ""
        private var url_ : String = ""

        // Constantes para permisos
        private const val LOCATION_PERMISSION_REQUEST_CODE: Int = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ConsultaQuejaBinding
    private lateinit var apiWeb: ApiWeb
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedFile: File? = null
    private lateinit var loadingDialog: AlertDialog

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConsultaQuejaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de herramientas
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        setupLoadingDialog()
        // Inicializar dependencias y configuración
        inicializarDependencias()
        obtenerDatosIntent()
        configurarVistas()
        configurarMapa()
        configurarBotones()
        hideLoadingDialog()

    }

    private fun inicializarDependencias() {
        apiWeb = configuracion.obtenerConfiguracionRetrofit()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun obtenerDatosIntent() {
        // Obtener datos del intent
        showLoadingDialog()
        vl_longitud = intent.getStringExtra(Constantes.VL_LONGITUD)?.toDoubleOrNull() ?: 0.0
        vl_latitud = intent.getStringExtra(Constantes.VL_LATITUD)?.toDoubleOrNull() ?: 0.0
        vl_quedadescripcion = intent.getStringExtra(Constantes.CQUEJA_DESCRIPCION) ?: ""
        vl_ruta_foto = intent.getStringExtra(Constantes.CRUTA_FOTO) ?: ""
        vl_estado = intent.getStringExtra(Constantes.CESTADO) ?: ""
        vl_categoria = intent.getStringExtra(Constantes.CCATEGORIA) ?: ""
        vl_fecha_hora = intent.getStringExtra(Constantes.CFECHA_HORA) ?: ""
    }

    private fun configurarVistas() {
        // Configurar campos de texto
        binding.lbCategoriaCQ.text = vl_categoria
        binding.lbSituacionCQ.text = vl_estado
        binding.lbFechaHoraCQ.text = vl_fecha_hora
        binding.lbDescripcionCQ2.text = vl_quedadescripcion

        // Configurar imagen
        val url = Constantes.url_fotoBase + vl_ruta_foto
        url_ = url

        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_image_placeholder) // Imagen de placeholder mientras carga
            .error(R.drawable.ic_error_image) // Imagen en caso de error
            .into(binding.imgFotoConsulta)

        // Configurar chip de estado
        configurarChipEstado()
    }

    private fun configurarChipEstado() {
        binding.chipEstado.text = vl_estado

        // Cambiar color según el estado
        when (vl_estado.toLowerCase()) {
            "pendiente" -> binding.chipEstado.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_naranja))
            "en proceso" -> binding.chipEstado.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_secundario))
            "completado", "atendido" -> binding.chipEstado.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_verde))
            else -> binding.chipEstado.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_primario))
        }
    }

    private fun configurarMapa() {
        // Crear un nuevo fragmento de mapa
        val mapFragment = SupportMapFragment.newInstance()

        // Agregarlo programáticamente al contenedor
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        // Obtener el mapa cuando esté listo
        mapFragment.getMapAsync(this)
    }

    private fun configurarBotones() {
        // Configurar botón de compartir
        binding.fabCompartir.setOnClickListener {
            compartirIncidente()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Añadir marcador en la ubicación del incidente
        val ubicacion = LatLng(vl_latitud, vl_longitud)
        mMap.addMarker(MarkerOptions()
            .position(ubicacion)
            .title("Ubicación del incidente"))

        // Mover cámara a la ubicación
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 19f))

        // Configurar botón de mi ubicación si el permiso está concedido
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            solicitarPermisoUbicacion()
        }
    }

    private fun solicitarPermisoUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Callback para actualizaciones de ubicación
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // Actualizaciones de ubicación disponibles aquí
                // No movemos la cámara para no interferir con la visualización del incidente
            }
        }
    }

    // Configuración de solicitud de ubicación
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun mostrarImagenCompleta(view: View?) {
        // Crear y mostrar diálogo a pantalla completa
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_imagen_completa)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreenImage)

        // Cargar imagen con Glide
        Glide.with(this)
            .load(url_)
            .into(imageView)

        // Cerrar al tocar
        imageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // Método para compartir información del incidente
    private fun compartirIncidente() {
        val mensaje = """
            Incidente reportado: $vl_categoria
            Estado: $vl_estado
            Ubicación: $vl_latitud, $vl_longitud
            Fecha y hora: $vl_fecha_hora
            Descripción: $vl_quedadescripcion
        """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, mensaje)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(intent, "Compartir incidente"))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, habilitar mi ubicación en el mapa
                try {
                    mMap.isMyLocationEnabled = true
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Error al activar ubicación", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Permiso denegado
                Toast.makeText(
                    this,
                    "Se requiere permiso de ubicación para mostrar tu posición",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Iniciar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onStop() {
        super.onStop()

        // Detener actualizaciones de ubicación para ahorrar batería
        fusedLocationClient.removeLocationUpdates(locationCallback)
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