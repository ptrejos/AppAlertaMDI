package com.example.appalertamdi.config

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object configuracion {

    val URL_BASE = "https://www.muniindependencia.gob.pe/ws_app/Plavin/"

    fun obtenerConfiguracionRetrofit(): ApiWeb {

        // Configurar logging para debugging (opcional pero recomendado)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Configurar OkHttpClient con timeouts extendidos
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)    // Tiempo para conectar al servidor
            .readTimeout(120, TimeUnit.SECONDS)      // Tiempo para leer respuesta (2 minutos)
            .writeTimeout(60, TimeUnit.SECONDS)      // Tiempo para enviar datos
            .callTimeout(180, TimeUnit.SECONDS)      // Timeout total (3 minutos)
            .retryOnConnectionFailure(true)          // Reintentar en fallos de conexión
            .addInterceptor(loggingInterceptor)      // Para ver logs de peticiones
            .build()

        // Configurar Gson con configuraciones específicas
        val gson = GsonBuilder()
            .setLenient()                            // Más tolerante con JSON mal formado
            .create()

        // Configurar Retrofit con OkHttpClient personalizado
        val objRetrofit = Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(okHttpClient)                    // ¡ESTO ES LO QUE FALTABA!
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiWeb = objRetrofit.create(ApiWeb::class.java)
        return apiWeb
    }
}