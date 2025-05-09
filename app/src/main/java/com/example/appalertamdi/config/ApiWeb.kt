package com.example.appalertamdi.config
import com.example.appalertamdi.entidad.Usuario
import com.example.appalertamdi.entidad.Categoria
import com.example.appalertamdi.entidad.Incidencia
import com.example.appalertamdi.entidad.QueryListadoQuejas
import com.example.appalertamdi.entidad.Respuesta
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiWeb {

    @GET( "{parametro}")
    fun validaUsuario(@Path("parametro") parametro:String, @Query("user") user: String, @Query("pass") pass: String):Call<List<Usuario>>

    @GET( "{parametro}")
    fun listarMenu(@Path("parametro") parametro:String):Call<ArrayList<Categoria>>

    @POST("registrarQuejaConArchivo")
    fun registrarIncidencia(@Body p:Incidencia):Call<Incidencia>

    @Multipart
    @POST("registrarQueja")
    fun registrarIncidencia_2(@Part("categoria_id") categoria_id: RequestBody,
                              @Part("usuario_id") usuario_id: RequestBody,
                              @Part("descripcion") descripcion: RequestBody,
                              @Part("latitud") latitud: RequestBody,
                              @Part("longitud") longitud: RequestBody,
                              //@Part("foto") foto: RequestBody
                              @Part foto: MultipartBody.Part
    ):Call<ResponseBody>

    @GET("{parametro}")
    fun listaQuejasXUsuario(@Path("parametro") parametro:String, @Query("id") id: Int):Call<ArrayList<QueryListadoQuejas>>
}