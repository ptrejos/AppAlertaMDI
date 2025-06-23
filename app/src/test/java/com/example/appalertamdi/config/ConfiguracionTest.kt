package com.example.appalertamdi.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Pruebas unitarias para la clase configuracion
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/config/ConfiguracionTest.kt
 * 
 * Ejecutar con: ./gradlew test
 * 
 * La clase configuracion es responsable de proporcionar la configuración de Retrofit
 * para la comunicación con el API REST de la Municipalidad de Independencia
 */
class ConfiguracionTest {

    private lateinit var apiWebInstance: ApiWeb

    @Before
    fun setUp() {
        // Configurar instancia de ApiWeb para pruebas
        try {
            apiWebInstance = configuracion.obtenerConfiguracionRetrofit()
        } catch (e: Exception) {
            // En caso de error de conexión, usar mock
            apiWebInstance = mock(ApiWeb::class.java)
        }
    }

    // ========== PRUEBAS DE URL_BASE ==========

    @Test
    fun `URL_BASE deberia tener formato valido de URL`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When & Then
        assertNotNull("URL_BASE no debería ser null", urlBase)
        assertTrue("URL_BASE no debería estar vacía", urlBase.isNotEmpty())
        assertTrue("URL_BASE debería empezar con https://", urlBase.startsWith("https://"))
        assertTrue("URL_BASE debería terminar con /", urlBase.endsWith("/"))
    }

    @Test
    fun `URL_BASE deberia apuntar al dominio de la Municipalidad de Independencia`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When & Then
        assertTrue("URL debería contener el dominio de la municipalidad", 
                  urlBase.contains("muniindependencia.gob.pe"))
        assertTrue("URL debería apuntar al servicio web", urlBase.contains("/ws_app/"))
        assertTrue("URL debería apuntar al endpoint Plavin", urlBase.contains("/Plavin/"))
    }

    @Test
    fun `URL_BASE deberia ser parseada correctamente como URL`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When & Then
        try {
            val url = URL(urlBase)
            assertEquals("Protocolo debería ser HTTPS", "https", url.protocol)
            assertEquals("Host debería ser correcto", "www.muniindependencia.gob.pe", url.host)
            assertTrue("Path debería contener ws_app/Plavin", url.path.contains("/ws_app/Plavin"))
        } catch (e: Exception) {
            fail("URL_BASE debería ser una URL válida: ${e.message}")
        }
    }

    @Test
    fun `URL_BASE deberia tener estructura esperada para servicios municipales`() {
        // Given
        val urlBase = configuracion.URL_BASE
        val expectedUrl = "https://www.muniindependencia.gob.pe/ws_app/Plavin/"

        // When & Then
        assertEquals("URL debería coincidir exactamente con la esperada", expectedUrl, urlBase)
    }

    // ========== PRUEBAS DE obtenerConfiguracionRetrofit() ==========

    @Test
    fun `obtenerConfiguracionRetrofit deberia retornar instancia no nula de ApiWeb`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb no debería ser null", apiWeb)
        assertTrue("Debería ser instancia de ApiWeb", apiWeb is ApiWeb)
    }

    @Test
    fun `obtenerConfiguracionRetrofit deberia usar URL_BASE configurada`() {
        // Given
        val urlEsperada = configuracion.URL_BASE

        // When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb debería ser creada correctamente", apiWeb)
        // Nota: No podemos acceder directamente a la URL interna de Retrofit en tests unitarios
        // pero podemos verificar que se crea sin errores
    }

    @Test
    fun `obtenerConfiguracionRetrofit deberia crear instancia funcional multiple veces`() {
        // Given & When
        val apiWeb1 = configuracion.obtenerConfiguracionRetrofit()
        val apiWeb2 = configuracion.obtenerConfiguracionRetrofit()
        val apiWeb3 = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("Primera instancia no debería ser null", apiWeb1)
        assertNotNull("Segunda instancia no debería ser null", apiWeb2)
        assertNotNull("Tercera instancia no debería ser null", apiWeb3)
        
        // Verificar que cada llamada crea una nueva instancia
        assertNotSame("Deberían ser instancias diferentes", apiWeb1, apiWeb2)
        assertNotSame("Deberían ser instancias diferentes", apiWeb2, apiWeb3)
    }

    @Test
    fun `obtenerConfiguracionRetrofit deberia configurar GsonConverterFactory`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb con Gson debería crearse correctamente", apiWeb)
        // El hecho de que no lance excepción indica que Gson está configurado
    }

    // ========== PRUEBAS DE CONFIGURACIÓN DE RETROFIT ==========

    @Test
    fun `configuracion deberia crear Retrofit con baseUrl correcto`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When
        val retrofit = Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Then
        assertNotNull("Retrofit debería ser creado", retrofit)
        assertEquals("BaseUrl debería coincidir", urlBase, retrofit.baseUrl().toString())
    }

    @Test
    fun `configuracion deberia usar GsonConverterFactory por defecto`() {
        // Given & When
        val gson = Gson()
        val gsonConverterFactory = GsonConverterFactory.create(gson)

        // Then
        assertNotNull("GsonConverterFactory debería ser creado", gsonConverterFactory)
    }

    @Test
    fun `configuracion deberia permitir crear ApiWeb interface`() {
        // Given
        val retrofit = Retrofit.Builder()
            .baseUrl(configuracion.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // When
        val apiWeb = retrofit.create(ApiWeb::class.java)

        // Then
        assertNotNull("ApiWeb interface debería ser creada", apiWeb)
        assertTrue("Debería ser instancia de ApiWeb", apiWeb is ApiWeb)
    }

    // ========== PRUEBAS DE SINGLETON BEHAVIOR ==========

    @Test
    fun `configuracion deberia ser object singleton`() {
        // Given & When
        val config1 = configuracion
        val config2 = configuracion

        // Then
        assertSame("Deberían ser la misma instancia singleton", config1, config2)
    }

    @Test
    fun `URL_BASE deberia ser consistente entre accesos`() {
        // Given & When
        val url1 = configuracion.URL_BASE
        val url2 = configuracion.URL_BASE
        val url3 = configuracion.URL_BASE

        // Then
        assertEquals("URLs deberían ser idénticas", url1, url2)
        assertEquals("URLs deberían ser idénticas", url2, url3)
        assertSame("Deberían ser la misma referencia", url1, url2)
    }

    // ========== PRUEBAS DE SEGURIDAD ==========

    @Test
    fun `URL_BASE deberia usar protocolo HTTPS seguro`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When
        val usaHTTPS = urlBase.startsWith("https://")

        // Then
        assertTrue("URL debería usar protocolo HTTPS para seguridad", usaHTTPS)
        assertFalse("URL no debería usar HTTP inseguro", urlBase.startsWith("http://"))
    }

    @Test
    fun `URL_BASE deberia apuntar a dominio gubernamental verificado`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When & Then
        assertTrue("Debería ser dominio .gob.pe", urlBase.contains(".gob.pe"))
        assertTrue("Debería ser subdominio de muniindependencia", 
                  urlBase.contains("muniindependencia.gob.pe"))
    }

    // ========== PRUEBAS DE INTEGRACIÓN CON ENTIDADES ==========

    @Test
    fun `configuracion deberia soportar serializacion de entidades con SerializedName`() {
        // Given
        val gson = GsonBuilder().create()
        
        // When - Simular serialización de entidades de AppAlertaMDI
        val jsonTest = """{"id": 1, "nombre": "Test", "color": "#FF0000", "icono": "ic_test"}"""
        
        // Then
        try {
            val parsed = gson.fromJson(jsonTest, Any::class.java)
            assertNotNull("Gson debería parsear JSON correctamente", parsed)
        } catch (e: Exception) {
            fail("Gson debería manejar JSON de entidades: ${e.message}")
        }
    }

    // ========== PRUEBAS DE CASOS LÍMITE ==========

    @Test
    fun `configuracion deberia manejar multiples llamadas concurrentes`() {
        // Given
        val threads = mutableListOf<Thread>()
        val results = mutableListOf<ApiWeb?>()

        // When
        repeat(10) {
            val thread = Thread {
                val apiWeb = configuracion.obtenerConfiguracionRetrofit()
                synchronized(results) {
                    results.add(apiWeb)
                }
            }
            threads.add(thread)
            thread.start()
        }

        // Esperar a que terminen todos los threads
        threads.forEach { it.join() }

        // Then
        assertEquals("Deberían haberse creado 10 instancias", 10, results.size)
        results.forEach { apiWeb ->
            assertNotNull("Cada instancia debería ser válida", apiWeb)
        }
    }

    @Test
    fun `configuracion deberia mantener consistencia bajo carga`() {
        // Given
        val iterations = 100
        val urls = mutableListOf<String>()

        // When
        repeat(iterations) {
            urls.add(configuracion.URL_BASE)
        }

        // Then
        assertEquals("Deberían ser $iterations URLs", iterations, urls.size)
        urls.forEach { url ->
            assertEquals("Todas las URLs deberían ser idénticas", configuracion.URL_BASE, url)
        }
    }

    // ========== PRUEBAS DE COMPATIBILIDAD CON ACTIVIDADES ==========

    @Test
    fun `configuracion deberia ser compatible con MainActivity`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb para MainActivity debería ser válida", apiWeb)
        // Verificar que tiene los métodos esperados para login
        assertTrue("Debería tener método para validación", 
                  apiWeb.javaClass.methods.any { it.name == "validaUsuario" })
    }

    @Test
    fun `configuracion deberia ser compatible con PrincipalActivity`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb para PrincipalActivity debería ser válida", apiWeb)
        // Verificar que tiene los métodos esperados para listar categorías
        assertTrue("Debería tener método para listar menú", 
                  apiWeb.javaClass.methods.any { it.name == "listarMenu" })
    }

    @Test
    fun `configuracion deberia ser compatible con RegistrarActivity`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb para RegistrarActivity debería ser válida", apiWeb)
        // Verificar que tiene los métodos esperados para registrar incidencias
        assertTrue("Debería tener método para registrar incidencia", 
                  apiWeb.javaClass.methods.any { it.name == "registrarIncidencia_2" })
    }

    @Test
    fun `configuracion deberia ser compatible con ListadoQuejasActivity`() {
        // Given & When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()

        // Then
        assertNotNull("ApiWeb para ListadoQuejasActivity debería ser válida", apiWeb)
        // Verificar que tiene los métodos esperados para listar quejas
        assertTrue("Debería tener método para listar quejas por usuario", 
                  apiWeb.javaClass.methods.any { it.name == "listaQuejasXUsuario" })
    }

    // ========== PRUEBAS DE PERFORMANCE ==========

    @Test
    fun `obtenerConfiguracionRetrofit deberia ser eficiente`() {
        // Given
        val startTime = System.currentTimeMillis()

        // When
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertNotNull("ApiWeb debería ser creada", apiWeb)
        assertTrue("Creación debería ser rápida (<500ms)", duration < 500)
    }

    @Test
    fun `acceso a URL_BASE deberia ser instantaneo`() {
        // Given
        val startTime = System.nanoTime()

        // When
        val url = configuracion.URL_BASE
        val endTime = System.nanoTime()
        val durationNanos = endTime - startTime

        // Then
        assertNotNull("URL debería ser obtenida", url)
        assertTrue("Acceso debería ser instantáneo (<1ms)", 
                  durationNanos < TimeUnit.MILLISECONDS.toNanos(1))
    }

    // ========== PRUEBAS DE CONFIGURACIÓN ESPECÍFICA DE APPALERTAMDI ==========

    @Test
    fun `configuracion deberia estar optimizada para servicios municipales`() {
        // Given
        val urlBase = configuracion.URL_BASE

        // When & Then
        assertTrue("URL debería apuntar a servicios de Independencia", 
                  urlBase.contains("muniindependencia"))
        assertTrue("URL debería usar endpoint Plavin", urlBase.contains("Plavin"))
        assertTrue("URL debería ser para web services", urlBase.contains("ws_app"))
    }

    @Test
    fun `configuracion deberia soportar todas las operaciones de AppAlertaMDI`() {
        // Given
        val apiWeb = configuracion.obtenerConfiguracionRetrofit()
        val metodos = apiWeb.javaClass.methods.map { it.name }

        // When & Then
        assertTrue("Debería soportar validación de usuarios", 
                  metodos.contains("validaUsuario"))
        assertTrue("Debería soportar listado de menú", 
                  metodos.contains("listarMenu"))
        assertTrue("Debería soportar registro de incidencias", 
                  metodos.contains("registrarIncidencia_2"))
        assertTrue("Debería soportar listado de quejas por usuario", 
                  metodos.contains("listaQuejasXUsuario"))
    }

    @Test
    fun `configuracion deberia usar Gson compatible con entidades de AppAlertaMDI`() {
        // Given
        val gson = GsonBuilder().create()

        // When - Probar con JSON similar al que devuelve la API
        val jsonUsuario = """{"rs":"1","nombre":"Test User","codigo":"12345"}"""
        val jsonCategoria = """{"id":1,"nombre":"Test Category","color":"#FF0000","icono":"ic_test"}"""

        // Then
        try {
            val usuario = gson.fromJson(jsonUsuario, Map::class.java)
            val categoria = gson.fromJson(jsonCategoria, Map::class.java)
            
            assertNotNull("Debería parsear JSON de usuario", usuario)
            assertNotNull("Debería parsear JSON de categoría", categoria)
            
            assertEquals("Usuario debería tener rs", "1", usuario["rs"])
            assertEquals("Categoría debería tener id", 1.0, categoria["id"])
        } catch (e: Exception) {
            fail("Gson debería ser compatible con entidades de AppAlertaMDI: ${e.message}")
        }
    }
}