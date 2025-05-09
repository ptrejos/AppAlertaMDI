package com.example.appalertamdi.entidad

import com.google.gson.annotations.SerializedName

class QueryListadoQuejas (
    @SerializedName("queja_id") var queja_id: Int,
    @SerializedName("queja_descripcion") var queja_descripcion: String,
    @SerializedName("foto") var foto: String,
    @SerializedName("latitud") var latitud: String,
    @SerializedName("longitud") var longitud: String,
    @SerializedName("fecha_hora") var fecha_hora: String,
    @SerializedName("estado_atencion") var estado_atencion: String,
    @SerializedName("descripcion_categoria") var descripcion_categoria: String,
    @SerializedName("color") var color: String,
    @SerializedName("icono") var icono: String,
    @SerializedName("ciudadano_codigo") var ciudadano_codigo: Int


)
