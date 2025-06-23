package com.example.appalertamdi.entidad

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

/*class Incidencia (
    @SerializedName("categoria_id") var categoria_id: Int,
    @SerializedName("usuario_id") var usuario_id: Int,
    @SerializedName("descripcion") var descripcion: String,
    @SerializedName("latitud") var latitud: Int,
    @SerializedName("longitud") var longitud: Int,
    @SerializedName("foto") var foto: MultipartBody.Part

)*/

class Incidencia (
    @Part("categoria_id") var categoria_id: RequestBody,
    @Part("usuario_id") var usuario_id: RequestBody,
    @Part("descripcion") var descripcion: RequestBody,
    @Part("latitud") var latitud: RequestBody,
    @Part("longitud") var longitud: RequestBody,
    @Part var foto: MultipartBody.Part

)


