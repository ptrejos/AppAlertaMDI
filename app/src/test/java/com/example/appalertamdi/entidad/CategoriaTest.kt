package com.example.appalertamdi.entidad

import android.graphics.Color
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Pruebas unitarias para la clase Categoria
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/entidad/CategoriaTest.kt
 * 
 * Ejecutar con: ./gradlew test
 * 
 * La clase Categoria representa los tipos de incidencias que pueden reportar los ciudadanos
 * en la aplicación AppAlertaMDI (Municipalidad de Independencia)
 */
class CategoriaTest {

    private lateinit var categoriaVialidad: Categoria
    private lateinit var categoriaLimpieza: Categoria
    private lateinit var categoriaAlumbrado: Categoria
    private lateinit var categoriaSeguridad: Categoria
    private lateinit var categoriaVacia: Categoria

    @Before
    fun setUp() {
        // Categorías típicas de una municipalidad
        categoriaVialidad = Categoria(
            id = 1,
            nombre = "Vialidad y Transporte",
            color = "#FF5722", // Naranja
            icono = "ic_road"
        )

        categoriaLimpieza = Categoria(
            id = 2,
            nombre = "Limpieza Pública",
            color = "#4CAF50", // Verde
            icono = "ic_cleaning"
        )

        categoriaAlumbrado = Categoria(
            id = 3,
            nombre = "Alumbrado Público",
            color = "#FFC107", // Amarillo
            icono = "ic_lightbulb"
        )

        categoriaSeguridad = Categoria(
            id = 4,
            nombre = "Seguridad Ciudadana",
            color = "#F44336", // Rojo
            icono = "ic_security"
        )

        categoriaVacia = Categoria(
            id = 0,
            nombre = "",
            color = "",
            icono = ""
        )
    }

    // ========== PRUEBAS DE CONSTRUCCIÓN ==========

    @Test
    fun `cuando se crea categoria con parametros validos entonces se inicializa correctamente`() {
        // Given & When (en setUp)
        val categoria = categoriaVialidad

        // Then
        assertEquals(1, categoria.id)
        assertEquals("Vialidad y Transporte", categoria.nombre)
        assertEquals("#FF5722", categoria.color)
        assertEquals("ic_road", categoria.icono)
    }

    @Test
    fun `categoria permite acceso a todas sus propiedades`() {
        // Given
        val categoria = categoriaLimpieza

        // When & Then
        assertEquals(2, categoria.id)
        assertEquals("Limpieza Pública", categoria.nombre)
        assertEquals("#4CAF50", categoria.color)
        assertEquals("ic_cleaning", categoria.icono)
    }

    @Test
    fun `categoria con valores vacios deberia crearse sin errores`() {
        // Given
        val categoria = categoriaVacia

        // When & Then
        assertEquals(0, categoria.id)
        assertEquals("", categoria.nombre)
        assertEquals("", categoria.color)
        assertEquals("", categoria.icono)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE ID ==========

    @Test
    fun `id deberia ser un numero positivo para categorias validas`() {
        // Given
        val categorias = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)

        // When & Then
        categorias.forEach { categoria ->
            assertTrue("ID debería ser positivo para ${categoria.nombre}", categoria.id > 0)
        }
    }

    @Test
    fun `id deberia ser unico entre categorias diferentes`() {
        // Given
        val categorias = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)

        // When
        val ids = categorias.map { it.id }
        val idsUnicos = ids.toSet()

        // Then
        assertEquals("Todos los IDs deberían ser únicos", ids.size, idsUnicos.size)
    }

    @Test
    fun `categoria con id negativo deberia manejarse`() {
        // Given
        val categoriaInvalida = Categoria(
            id = -1,
            nombre = "Categoría Inválida",
            color = "#000000",
            icono = "ic_error"
        )

        // When
        val idEsNegativo = categoriaInvalida.id < 0

        // Then
        assertTrue("ID negativo debería ser detectado", idEsNegativo)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE NOMBRE ==========

    @Test
    fun `nombre no deberia estar vacio para categorias validas`() {
        // Given
        val categoriasValidas = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)

        // When & Then
        categoriasValidas.forEach { categoria ->
            assertTrue("Nombre no debería estar vacío para ${categoria.nombre}", 
                      categoria.nombre.isNotEmpty())
        }
    }

    @Test
    fun `nombre deberia tener longitud razonable`() {
        // Given
        val categoria = categoriaVialidad

        // When
        val longitudNombre = categoria.nombre.length

        // Then
        assertTrue("Nombre debería tener al menos 3 caracteres", longitudNombre >= 3)
        assertTrue("Nombre no debería ser excesivamente largo", longitudNombre <= 50)
    }

    @Test
    fun `nombre deberia contener solo caracteres validos`() {
        // Given
        val categoria = categoriaLimpieza

        // When
        val tieneCaracteresEspeciales = categoria.nombre.contains(Regex("[^a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]"))

        // Then
        assertFalse("Nombre no debería contener caracteres especiales no permitidos", 
                   tieneCaracteresEspeciales)
    }

    @Test
    fun `categoria con nombre muy largo deberia manejarse`() {
        // Given
        val nombreLargo = "A".repeat(100)
        val categoria = Categoria(
            id = 99,
            nombre = nombreLargo,
            color = "#000000",
            icono = "ic_test"
        )

        // When
        val longitudNombre = categoria.nombre.length

        // Then
        assertEquals(100, longitudNombre)
        assertNotNull("Categoría con nombre largo debería crearse", categoria)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE COLOR ==========

    @Test
    fun `color deberia ser un codigo hexadecimal valido`() {
        // Given
        val categoriasConColor = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)

        // When & Then
        categoriasConColor.forEach { categoria ->
            assertTrue("Color debería empezar con #", categoria.color.startsWith("#"))
            assertTrue("Color debería tener 7 caracteres (#RRGGBB)", categoria.color.length == 7)
            
            // Verificar que los caracteres después de # sean hexadecimales
            val hexPart = categoria.color.substring(1)
            assertTrue("Color debería ser hexadecimal válido para ${categoria.nombre}", 
                      hexPart.matches(Regex("[0-9A-Fa-f]{6}")))
        }
    }

    @Test
    fun `color deberia poder parsearse como color de Android`() {
        // Given
        val categoria = categoriaVialidad

        // When & Then
        try {
            val colorInt = Color.parseColor(categoria.color)
            assertTrue("Color parseado debería ser válido", colorInt != 0 || categoria.color == "#000000")
        } catch (e: IllegalArgumentException) {
            fail("Color debería ser válido para parsing: ${categoria.color}")
        }
    }

    @Test
    fun `colores deberian ser visualmente distinguibles`() {
        // Given
        val colores = listOf(
            categoriaVialidad.color,    // #FF5722 - Naranja
            categoriaLimpieza.color,    // #4CAF50 - Verde
            categoriaAlumbrado.color,   // #FFC107 - Amarillo
            categoriaSeguridad.color    // #F44336 - Rojo
        )

        // When
        val coloresUnicos = colores.toSet()

        // Then
        assertEquals("Todos los colores deberían ser únicos", colores.size, coloresUnicos.size)
    }

    @Test
    fun `color invalido deberia manejarse graciosamente`() {
        // Given
        val categoriaColorInvalido = Categoria(
            id = 99,
            nombre = "Test",
            color = "color_invalido",
            icono = "ic_test"
        )

        // When
        val colorEsInvalido = !categoriaColorInvalido.color.startsWith("#")

        // Then
        assertTrue("Color inválido debería ser detectado", colorEsInvalido)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE ICONO ==========

    @Test
    fun `icono deberia tener formato de nombre de recurso valido`() {
        // Given
        val categoriasConIcono = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)

        // When & Then
        categoriasConIcono.forEach { categoria ->
            assertTrue("Icono debería empezar con 'ic_'", categoria.icono.startsWith("ic_"))
            assertTrue("Icono no debería estar vacío", categoria.icono.isNotEmpty())
            assertFalse("Icono no debería contener espacios", categoria.icono.contains(" "))
            assertTrue("Icono debería contener solo caracteres válidos", 
                      categoria.icono.matches(Regex("[a-z_0-9]+")))
        }
    }

    @Test
    fun `iconos deberian ser descriptivos para su categoria`() {
        // Given & When & Then
        assertTrue("Icono de vialidad debería ser descriptivo", 
                  categoriaVialidad.icono.contains("road") || 
                  categoriaVialidad.icono.contains("transport"))
        
        assertTrue("Icono de limpieza debería ser descriptivo", 
                  categoriaLimpieza.icono.contains("clean"))
        
        assertTrue("Icono de alumbrado debería ser descriptivo", 
                  categoriaAlumbrado.icono.contains("light") || 
                  categoriaAlumbrado.icono.contains("bulb"))
        
        assertTrue("Icono de seguridad debería ser descriptivo", 
                  categoriaSeguridad.icono.contains("security") || 
                  categoriaSeguridad.icono.contains("shield"))
    }

    // ========== PRUEBAS DE MODIFICACIÓN ==========

    @Test
    fun `categoria permite modificar todas sus propiedades`() {
        // Given
        val categoria = Categoria(
            id = 1,
            nombre = "Original",
            color = "#000000",
            icono = "ic_original"
        )

        // When
        categoria.id = 2
        categoria.nombre = "Modificado"
        categoria.color = "#FFFFFF"
        categoria.icono = "ic_modificado"

        // Then
        assertEquals(2, categoria.id)
        assertEquals("Modificado", categoria.nombre)
        assertEquals("#FFFFFF", categoria.color)
        assertEquals("ic_modificado", categoria.icono)
    }

    @Test
    fun `modificar categoria no afecta otras instancias`() {
        // Given
        val categoria1 = Categoria(1, "Test1", "#FF0000", "ic_test1")
        val categoria2 = Categoria(2, "Test2", "#00FF00", "ic_test2")

        // When
        categoria1.nombre = "Modificado"

        // Then
        assertEquals("Modificado", categoria1.nombre)
        assertEquals("Test2", categoria2.nombre)
    }

    // ========== PRUEBAS DE CASOS DE USO ESPECÍFICOS ==========

    @Test
    fun `categoria deberia funcionar correctamente en adaptador de menu`() {
        // Given
        val categoria = categoriaVialidad

        // When - Simular uso en MenuAdaptador
        val textoParaMostrar = categoria.nombre
        val colorParaFondo = categoria.color
        val iconoParaMostrar = categoria.icono
        val idParaIntent = categoria.id.toString()

        // Then
        assertNotNull("Texto para mostrar no debería ser null", textoParaMostrar)
        assertNotNull("Color para fondo no debería ser null", colorParaFondo)
        assertNotNull("Icono para mostrar no debería ser null", iconoParaMostrar)
        assertNotNull("ID para intent no debería ser null", idParaIntent)
        assertTrue("ID para intent debería ser convertible a String", idParaIntent.isNotEmpty())
    }

    @Test
    fun `categoria deberia poder usarse para filtrado`() {
        // Given
        val categorias = listOf(categoriaVialidad, categoriaLimpieza, categoriaAlumbrado, categoriaSeguridad)
        val textoBusqueda = "Limpieza"

        // When
        val categoriasFiltradas = categorias.filter { 
            it.nombre.contains(textoBusqueda, ignoreCase = true) 
        }

        // Then
        assertEquals(1, categoriasFiltradas.size)
        assertEquals(categoriaLimpieza.id, categoriasFiltradas.first().id)
    }

    // ========== PRUEBAS DE COMPARACIÓN ==========

    @Test
    fun `dos categorias con mismo id deberian considerarse equivalentes`() {
        // Given
        val categoria1 = Categoria(1, "Nombre1", "#FF0000", "ic_test1")
        val categoria2 = Categoria(1, "Nombre2", "#00FF00", "ic_test2")

        // When
        val mismoId = categoria1.id == categoria2.id

        // Then
        assertTrue("Categorías con mismo ID deberían considerarse equivalentes", mismoId)
    }

    @Test
    fun `categoria deberia poder convertirse a string representativo`() {
        // Given
        val categoria = categoriaVialidad

        // When
        fun Categoria.toStringRepresentativo(): String {
            return "Categoria(id=$id, nombre='$nombre', color='$color', icono='$icono')"
        }

        // Then
        val expected = "Categoria(id=1, nombre='Vialidad y Transporte', color='#FF5722', icono='ic_road')"
        assertEquals(expected, categoria.toStringRepresentativo())
    }

    // ========== PRUEBAS DE INTEGRACIÓN CON API ==========

    @Test
    fun `categoria deberia tener campos compatibles con SerializedName`() {
        // Given
        val categoria = categoriaVialidad

        // When & Then
        // Verificamos que los campos están presentes y son accesibles
        // (las anotaciones @SerializedName se verificarían en pruebas de integración)
        assertNotNull("Campo id debe estar presente", categoria.id)
        assertNotNull("Campo nombre debe estar presente", categoria.nombre)
        assertNotNull("Campo color debe estar presente", categoria.color)
        assertNotNull("Campo icono debe estar presente", categoria.icono)
    }

    @Test
    fun `categoria deberia manejar datos de ejemplo de la API`() {
        // Given - Datos que podrían venir de la API real
        val categoriaDesdeAPI = Categoria(
            id = 5,
            nombre = "Mantenimiento de Parques",
            color = "#8BC34A",
            icono = "ic_park"
        )

        // When & Then
        assertEquals(5, categoriaDesdeAPI.id)
        assertEquals("Mantenimiento de Parques", categoriaDesdeAPI.nombre)
        assertEquals("#8BC34A", categoriaDesdeAPI.color)
        assertEquals("ic_park", categoriaDesdeAPI.icono)
    }

    // ========== PRUEBAS DE CATEGORÍAS MUNICIPALES TÍPICAS ==========

    @Test
    fun `deberia manejar categorias tipicas de municipalidad`() {
        // Given
        val categoriasMunicipales = listOf(
            Categoria(1, "Baches y Vialidad", "#FF5722", "ic_road"),
            Categoria(2, "Recolección de Basura", "#4CAF50", "ic_trash"),
            Categoria(3, "Alumbrado Público", "#FFC107", "ic_lightbulb"),
            Categoria(4, "Seguridad Ciudadana", "#F44336", "ic_security"),
            Categoria(5, "Mantenimiento de Parques", "#8BC34A", "ic_park"),
            Categoria(6, "Problemas de Agua", "#2196F3", "ic_water"),
            Categoria(7, "Ruidos Molestos", "#9C27B0", "ic_volume"),
            Categoria(8, "Infraestructura", "#607D8B", "ic_construction")
        )

        // When & Then
        categoriasMunicipales.forEach { categoria ->
            assertTrue("ID debería ser positivo", categoria.id > 0)
            assertTrue("Nombre no debería estar vacío", categoria.nombre.isNotEmpty())
            assertTrue("Color debería ser válido", categoria.color.startsWith("#"))
            assertTrue("Icono debería ser válido", categoria.icono.startsWith("ic_"))
        }

        assertEquals(8, categoriasMunicipales.size)
    }
}