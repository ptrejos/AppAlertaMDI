// src/test/java/com/example/appalertamdi/entidad/QueryListadoQuejasTest.kt
package com.example.appalertamdi.entidad

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class QueryListadoQuejasTest {

    @Test
    fun queryListadoQuejas_instantiationAndPropertyAssignment_isCorrect() {
        // Valores de ejemplo para instanciar la clase
        val queja_id = 1
        val queja_descripcion = "Descripción de prueba"
        val foto = "url_foto.jpg"
        val latitud = "10.123"
        val longitud = "-70.456"
        val fecha_hora = "2023-10-27 10:00:00"
        val estado_atencion = "Pendiente"
        val descripcion_categoria = "Calles"
        val color = "#FF0000"
        val icono = "icono.png"
        val ciudadano_codigo = 101

        // When (Cuando): Se instancia el objeto QueryListadoQuejas
        val query = QueryListadoQuejas(
            queja_id,
            queja_descripcion,
            foto,
            latitud,
            longitud,
            fecha_hora,
            estado_atencion,
            descripcion_categoria,
            color,
            icono,
            ciudadano_codigo
        )

        //  Se verifican que las propiedades se hayan asignado correctamente
        assertNotNull("El objeto QueryListadoQuejas no debería ser nulo", query)
        assertEquals(queja_id, query.queja_id)
        assertEquals(queja_descripcion, query.queja_descripcion)
        assertEquals(foto, query.foto)
        assertEquals(latitud, query.latitud)
        assertEquals(longitud, query.longitud)
        assertEquals(fecha_hora, query.fecha_hora)
        assertEquals(estado_atencion, query.estado_atencion)
        assertEquals(descripcion_categoria, query.descripcion_categoria)
        assertEquals(color, query.color)
        assertEquals(icono, query.icono)
        assertEquals(ciudadano_codigo, query.ciudadano_codigo)
    }


}