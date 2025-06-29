// src/test/java/com/example/appalertamdi/config/ConfiguracionTest.kt
package com.example.appalertamdi.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.Before //

class ConfiguracionTest {

    @Test
    fun obtenerConfiguracionRetrofit_returnsNonNullApiWebInstance() {

        val apiWebInstance = configuracion.obtenerConfiguracionRetrofit()

        // Then (Entonces) - Se verifica que la instancia no sea nula
        assertNotNull("La instancia de ApiWeb no debería ser nula", apiWebInstance)
    }

    @Test
    fun URL_BASE_isCorrect() {

        // When - Accedemos a la constante
        val expectedUrl = "https://www.muniindependencia.gob.pe/ws_app/Plavin/"
        val actualUrl = configuracion.URL_BASE

        // Then - Verificamos que la URL sea la esperada
        assertEquals("La URL_BASE debería ser la esperada", expectedUrl, actualUrl)
    }

    // Ejemplo de cómo se vería una prueba que verifique que no hay excepciones
    @Test
    fun obtenerConfiguracionRetrofit_doesNotThrowException() {

        // Si la llamada a la función lanza una excepción, la prueba fallará.
        // Si no lanza ninguna excepción y termina, la prueba pasa.
        configuracion.obtenerConfiguracionRetrofit()

    }
}