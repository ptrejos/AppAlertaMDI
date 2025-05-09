package com.example.appalertamdi.config

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object configuracion {

    val URL_BASE = "https://www.muniindependencia.gob.pe/ws_app/Plavin/"



    fun obtenerConfiguracionRetrofit():ApiWeb{
        var objRetrofit = Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var apiWeb = objRetrofit.create(ApiWeb::class.java)
        return  apiWeb
    }
}