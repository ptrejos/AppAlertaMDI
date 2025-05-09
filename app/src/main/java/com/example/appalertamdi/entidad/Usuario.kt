package com.example.appalertamdi.entidad

import com.google.gson.annotations.SerializedName

class Usuario (
    @SerializedName("rs") var rs: String,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("codigo") var codigo: String
)