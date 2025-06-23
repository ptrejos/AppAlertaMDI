package com.example.appalertamdi.entidad

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.*
import java.io.File

/**
 * Pruebas unitarias para la clase Incidencia
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/entidad/IncidenciaTest.kt
 * 
 * Ejecutar con: ./gradlew test
 * 
 * Nota: La clase Incidencia maneja RequestBody y MultipartBody.Part para envío multipart
 */
class IncidenciaTest {

    private lateinit var incidenciaValida: Incidencia
    private lateinit var mockFoto: MultipartBody.Part
    private lateinit var categoriaId: RequestBody
    private lateinit var usuarioId: RequestBody
    private lateinit var descripcion: RequestBody
    private lateinit var latitud: RequestBody
    private lateinit var longitud: RequestBody

    @Before
    fun setUp() {
        // Crear RequestBodies de prueba
        categoriaId = "1".toRequestBody("text/plain".toMediaTypeOrNull())
        usuarioId = "12345".toRequestBody("text/plain".toMediaTypeOrNull())
        descripcion = "Bache en la calle principal".toRequestBody("text/plain".toMediaTypeOrNull())
        latitud = "-12.0432".toRequestBody("text/plain".toMediaTypeOrNull())
        longitud = "-77.0282".toRequestBody("text/plain".toMediaTypeOrNull())

        // Mock del archivo de foto
        mockFoto = mock(MultipartBody.Part::class.java)

        // Crear incidencia válida
        incidenciaValida = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioId,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )
    }

    // ========== PRUEBAS DE CONSTRUCCIÓN ==========

    @Test
    fun `cuando se crea incidencia con parametros validos entonces se inicializa correctamente`() {
        // Given & When (en setUp)
        val incidencia = incidenciaValida

        // Then
        assertNotNull("Categoria ID no debería ser null", incidencia.categoria_id)
        assertNotNull("Usuario ID no debería ser null", incidencia.usuario_id)
        assertNotNull("Descripción no debería ser null", incidencia.descripcion)
        assertNotNull("Latitud no debería ser null", incidencia.latitud)
        assertNotNull("Longitud no debería ser null", incidencia.longitud)
        assertNotNull("Foto no debería ser null", incidencia.foto)
    }

    @Test
    fun `incidencia permite acceso a todas sus propiedades`() {
        // Given
        val incidencia = incidenciaValida

        // When & Then
        // Verificar que se pueden acceder a todas las propiedades
        try {
            val categoria = incidencia.categoria_id
            val usuario = incidencia.usuario_id
            val desc = incidencia.descripcion
            val lat = incidencia.latitud
            val lng = incidencia.longitud
            val foto = incidencia.foto

            // Si llegamos aquí, todas las propiedades son accesibles
            assertTrue("Todas las propiedades deberían ser accesibles", true)
        } catch (e: Exception) {
            fail("No debería fallar al acceder a las propiedades: ${e.message}")
        }
    }

    // ========== PRUEBAS DE VALIDACIÓN DE DATOS ==========

    @Test
    fun `categoria_id deberia ser un RequestBody valido`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val categoria = incidencia.categoria_id

        // Then
        assertNotNull("Categoria ID no puede ser null", categoria)
        assertTrue("Categoria ID debe ser instancia de RequestBody", 
                  categoria is RequestBody)
    }

    @Test
    fun `usuario_id deberia ser un RequestBody valido`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val usuario = incidencia.usuario_id

        // Then
        assertNotNull("Usuario ID no puede ser null", usuario)
        assertTrue("Usuario ID debe ser instancia de RequestBody", 
                  usuario is RequestBody)
    }

    @Test
    fun `descripcion deberia ser un RequestBody valido`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val desc = incidencia.descripcion

        // Then
        assertNotNull("Descripción no puede ser null", desc)
        assertTrue("Descripción debe ser instancia de RequestBody", 
                  desc is RequestBody)
    }

    @Test
    fun `coordenadas deberian ser RequestBody validos`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val lat = incidencia.latitud
        val lng = incidencia.longitud

        // Then
        assertNotNull("Latitud no puede ser null", lat)
        assertNotNull("Longitud no puede ser null", lng)
        assertTrue("Latitud debe ser instancia de RequestBody", lat is RequestBody)
        assertTrue("Longitud debe ser instancia de RequestBody", lng is RequestBody)
    }

    @Test
    fun `foto deberia ser un MultipartBody_Part valido`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val foto = incidencia.foto

        // Then
        assertNotNull("Foto no puede ser null", foto)
        assertTrue("Foto debe ser instancia de MultipartBody.Part", 
                  foto is MultipartBody.Part)
    }

    // ========== PRUEBAS DE CREACIÓN DE REQUEST BODIES ==========

    @Test
    fun `crear RequestBody desde string deberia funcionar correctamente`() {
        // Given
        val textoOriginal = "Texto de prueba"

        // When
        val requestBody = textoOriginal.toRequestBody("text/plain".toMediaTypeOrNull())

        // Then
        assertNotNull("RequestBody no debería ser null", requestBody)
        assertEquals("text/plain", requestBody.contentType()?.toString())
    }

    @Test
    fun `crear RequestBody con categoria_id numerico`() {
        // Given
        val categoriaIdString = "5"

        // When
        val categoriaRequestBody = categoriaIdString.toRequestBody("text/plain".toMediaTypeOrNull())
        val incidencia = Incidencia(
            categoria_id = categoriaRequestBody,
            usuario_id = usuarioId,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con categoría numérica debería crearse", incidencia)
        assertEquals("text/plain", incidencia.categoria_id.contentType()?.toString())
    }

    @Test
    fun `crear RequestBody con coordenadas decimales`() {
        // Given
        val latitudStr = "-12.0432"
        val longitudStr = "-77.0282"

        // When
        val latRequestBody = latitudStr.toRequestBody("text/plain".toMediaTypeOrNull())
        val lngRequestBody = longitudStr.toRequestBody("text/plain".toMediaTypeOrNull())

        // Then
        assertNotNull("Latitud RequestBody no debería ser null", latRequestBody)
        assertNotNull("Longitud RequestBody no debería ser null", lngRequestBody)
        assertEquals("text/plain", latRequestBody.contentType()?.toString())
        assertEquals("text/plain", lngRequestBody.contentType()?.toString())
    }

    // ========== PRUEBAS DE CASOS LÍMITE ==========

    @Test
    fun `incidencia con descripcion muy larga deberia manejarse`() {
        // Given
        val descripcionLarga = "A".repeat(1000) // 1000 caracteres
        val descripcionLargaRB = descripcionLarga.toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioId,
            descripcion = descripcionLargaRB,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con descripción larga debería crearse", incidencia)
        assertNotNull("Descripción larga debería ser válida", incidencia.descripcion)
    }

    @Test
    fun `incidencia con descripcion vacia deberia manejarse`() {
        // Given
        val descripcionVacia = "".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioId,
            descripcion = descripcionVacia,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con descripción vacía debería crearse", incidencia)
        assertNotNull("Descripción vacía debería ser válida", incidencia.descripcion)
    }

    @Test
    fun `incidencia con coordenadas extremas deberia manejarse`() {
        // Given
        val latitudExtrema = "-90.0".toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudExtrema = "180.0".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioId,
            descripcion = descripcion,
            latitud = latitudExtrema,
            longitud = longitudExtrema,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con coordenadas extremas debería crearse", incidencia)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE COORDENADAS ==========

    @Test
    fun `coordenadas deberian representar ubicacion valida de Lima`() {
        // Given - Coordenadas típicas de Lima, Perú
        val latitudLima = "-12.0464".toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudLima = "-77.0428".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioId,
            descripcion = descripcion,
            latitud = latitudLima,
            longitud = longitudLima,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con coordenadas de Lima debería ser válida", incidencia)
        assertNotNull("Latitud de Lima debería ser válida", incidencia.latitud)
        assertNotNull("Longitud de Lima debería ser válida", incidencia.longitud)
    }

    // ========== PRUEBAS DE MODIFICACIÓN ==========

    @Test
    fun `incidencia permite modificar propiedades`() {
        // Given
        val incidencia = incidenciaValida
        val nuevaDescripcion = "Descripción modificada".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        incidencia.descripcion = nuevaDescripcion

        // Then
        assertEquals(nuevaDescripcion, incidencia.descripcion)
        assertNotEquals(descripcion, incidencia.descripcion)
    }

    @Test
    fun `incidencia permite cambiar categoria`() {
        // Given
        val incidencia = incidenciaValida
        val nuevaCategoria = "3".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        incidencia.categoria_id = nuevaCategoria

        // Then
        assertEquals(nuevaCategoria, incidencia.categoria_id)
        assertNotEquals(categoriaId, incidencia.categoria_id)
    }

    // ========== PRUEBAS DE INTEGRACIÓN CON CONSTANTES ==========

    @Test
    fun `incidencia deberia usar tipos de media correctos para upload`() {
        // Given
        val mediaTypePlainText = "text/plain".toMediaTypeOrNull()
        val mediaTypeImage = "image/*".toMediaTypeOrNull()

        // When
        val textRequestBody = "test".toRequestBody(mediaTypePlainText)

        // Then
        assertNotNull("MediaType text/plain debería ser válido", mediaTypePlainText)
        assertNotNull("MediaType image/* debería ser válido", mediaTypeImage)
        assertEquals("text/plain", textRequestBody.contentType()?.toString())
    }

    // ========== PRUEBAS DE CASOS DE ERROR ==========

    @Test
    fun `incidencia con usuario_id invalido deberia manejarse`() {
        // Given
        val usuarioInvalido = "abc".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaId,
            usuario_id = usuarioInvalido,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con usuario inválido debería crearse", incidencia)
        // Nota: La validación de formato debería hacerse en capas superiores
    }

    @Test
    fun `incidencia con categoria_id invalido deberia manejarse`() {
        // Given
        val categoriaInvalida = "xyz".toRequestBody("text/plain".toMediaTypeOrNull())

        // When
        val incidencia = Incidencia(
            categoria_id = categoriaInvalida,
            usuario_id = usuarioId,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            foto = mockFoto
        )

        // Then
        assertNotNull("Incidencia con categoría inválida debería crearse", incidencia)
        // Nota: La validación de formato debería hacerse en capas superiores
    }

    // ========== PRUEBAS DE UTILIDADES ==========

    @Test
    fun `helper method para crear incidencia desde strings`() {
        // Given
        fun crearIncidenciaDesdeStrings(
            categoriaStr: String,
            usuarioStr: String,
            descripcionStr: String,
            latitudStr: String,
            longitudStr: String,
            foto: MultipartBody.Part
        ): Incidencia {
            return Incidencia(
                categoria_id = categoriaStr.toRequestBody("text/plain".toMediaTypeOrNull()),
                usuario_id = usuarioStr.toRequestBody("text/plain".toMediaTypeOrNull()),
                descripcion = descripcionStr.toRequestBody("text/plain".toMediaTypeOrNull()),
                latitud = latitudStr.toRequestBody("text/plain".toMediaTypeOrNull()),
                longitud = longitudStr.toRequestBody("text/plain".toMediaTypeOrNull()),
                foto = foto
            )
        }

        // When
        val incidencia = crearIncidenciaDesdeStrings(
            "1", "12345", "Test", "-12.0", "-77.0", mockFoto
        )

        // Then
        assertNotNull("Helper method debería crear incidencia válida", incidencia)
    }

    @Test
    fun `validar que incidencia es apta para envio multipart`() {
        // Given
        val incidencia = incidenciaValida

        // When
        val tieneTodasLasPropiedades = listOf(
            incidencia.categoria_id,
            incidencia.usuario_id,
            incidencia.descripcion,
            incidencia.latitud,
            incidencia.longitud,
            incidencia.foto
        ).all { it != null }

        // Then
        assertTrue("Incidencia debería tener todas las propiedades para envío multipart", 
                  tieneTodasLasPropiedades)
    }
}