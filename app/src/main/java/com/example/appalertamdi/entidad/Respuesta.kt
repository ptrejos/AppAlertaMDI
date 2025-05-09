package com.example.appalertamdi.entidad

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.http.Part

data class Respuesta(
    @SerializedName("rs") var rs: String,
    @SerializedName("msg") var msg: String,
    //@Part("rs") var rs: RequestBody,
    //@Part("msg") var msg: RequestBody,
)