package com.example.appalertamdi.entidad

import com.google.gson.annotations.SerializedName

class Categoria (
    @SerializedName("id") var id: Int,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("color") var color: String,
    @SerializedName("icono") var icono: String
)