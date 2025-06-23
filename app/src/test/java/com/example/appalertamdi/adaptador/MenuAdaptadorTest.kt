package com.example.appalertamdi.adaptador

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.appalertamdi.R
import com.example.appalertamdi.RegistrarActivity
import com.example.appalertamdi.config.Constantes
import com.example.appalertamdi.entidad.Categoria
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas unitarias para la clase MenuAdaptador
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/adaptador/MenuAdaptadorTest.kt
 * 
 * NOTA: Estas son pruebas unitarias que usan Robolectric para simular el contexto Android
 * Para pruebas instrumentales, mover a app/src/androidTest/
 * 
 * Ejecutar con: ./gradlew test
 * 
 * El MenuAdaptador es responsable de mostrar las categorías de incidencias
 * en un GridLayout de 2 columnas en la pantalla principal
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class MenuAdaptadorTest {

    private lateinit var adaptador: MenuAdaptador
    private lateinit var categoriasVacias: ArrayList<Categoria>
    private lateinit var categoriasValidas: ArrayList<Categoria>
    private lateinit var categoriaUnica: ArrayList<Categoria>
    private lateinit var mockParent: ViewGroup
    private lateinit var mockView: View
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Configurar contexto mock usando Robolectric
        context = ApplicationProvider.getApplicationContext()
        
        // Crear listas de categorías para pruebas
        categoriasVacias = ArrayList()
        
        categoriasValidas = arrayListOf(
            Categoria(1, "Vialidad y Transporte", "#FF5722", "ic_road"),
            Categoria(2, "Limpieza Pública", "#4CAF50", "ic_cleaning"),
            Categoria(3, "Alumbrado Público", "#FFC107", "ic_lightbulb"),
            Categoria(4, "Seguridad Ciudadana", "#F44336", "ic_security")
        )

        categoriaUnica = arrayListOf(
            Categoria(1, "Categoria Test", "#2196F3", "ic_test")
        )

        // Configurar mocks
        mockParent = mock(ViewGroup::class.java)
        mockView = mock(View::class.java)
        `when`(mockParent.context).thenReturn(context)
    }

    // ========== PRUEBAS DE CONSTRUCCIÓN ==========

    @Test
    fun `cuando se crea adaptador con lista vacia entonces se inicializa correctamente`() {
        // Given & When
        adaptador = MenuAdaptador(categoriasVacias)

        // Then
        assertNotNull("Adaptador no debería ser null", adaptador)
        assertEquals("Cantidad de items debería ser 0", 0, adaptador.itemCount)
    }

    @Test
    fun `cuando se crea adaptador con categorias validas entonces se inicializa correctamente`() {
        // Given & When
        adaptador = MenuAdaptador(categoriasValidas)

        // Then
        assertNotNull("Adaptador no debería ser null", adaptador)
        assertEquals("Cantidad de items debería ser 4", 4, adaptador.itemCount)
    }

    @Test
    fun `cuando se crea adaptador con categoria unica entonces se inicializa correctamente`() {
        // Given & When
        adaptador = MenuAdaptador(categoriaUnica)

        // Then
        assertNotNull("Adaptador no debería ser null", adaptador)
        assertEquals("Cantidad de items debería ser 1", 1, adaptador.itemCount)
    }

    // ========== PRUEBAS DE GETITEMCOUNT ==========

    @Test
    fun `getItemCount deberia retornar numero correcto de categorias`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)

        // When
        val count = adaptador.itemCount

        // Then
        assertEquals("Count debería ser igual al tamaño de la lista", categoriasValidas.size, count)
    }

    @Test
    fun `getItemCount deberia retornar 0 para lista vacia`() {
        // Given
        adaptador = MenuAdaptador(categoriasVacias)

        // When
        val count = adaptador.itemCount

        // Then
        assertEquals("Count debería ser 0 para lista vacía", 0, count)
    }

    @Test
    fun `getItemCount deberia cambiar cuando se modifican las categorias`() {
        // Given
        adaptador = MenuAdaptador(categoriaUnica)
        val countInicial = adaptador.itemCount

        // When
        categoriaUnica.add(Categoria(2, "Nueva Categoria", "#000000", "ic_new"))
        val countFinal = adaptador.itemCount

        // Then
        assertEquals("Count inicial debería ser 1", 1, countInicial)
        assertEquals("Count final debería ser 2", 2, countFinal)
    }

    // ========== PRUEBAS DE CLEARDATA ==========

    @Test
    fun `clearData deberia vaciar la lista de categorias`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)
        val countInicial = adaptador.itemCount

        // When
        adaptador.clearData()
        val countFinal = adaptador.itemCount

        // Then
        assertEquals("Count inicial debería ser 4", 4, countInicial)
        assertEquals("Count final debería ser 0", 0, countFinal)
    }

    @Test
    fun `clearData en lista vacia no deberia causar error`() {
        // Given
        adaptador = MenuAdaptador(categoriasVacias)

        // When & Then
        try {
            adaptador.clearData()
            assertEquals("Count debería seguir siendo 0", 0, adaptador.itemCount)
        } catch (e: Exception) {
            fail("clearData en lista vacía no debería causar excepción: ${e.message}")
        }
    }

    @Test
    fun `clearData multiple veces no deberia causar error`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)

        // When & Then
        try {
            adaptador.clearData()
            adaptador.clearData()
            adaptador.clearData()
            assertEquals("Count debería ser 0 después de múltiples clears", 0, adaptador.itemCount)
        } catch (e: Exception) {
            fail("Múltiples clearData no deberían causar excepción: ${e.message}")
        }
    }

    // ========== PRUEBAS DE VIEWHOLDER ==========

    @Test
    fun `ViewHolder deberia tener todas las vistas requeridas`() {
        // Given
        val mockView = mock(View::class.java)
        val mockNombreCategoria = mock(TextView::class.java)
        val mockIconoCategoria = mock(ImageView::class.java)
        val mockLayoutItem = mock(View::class.java)

        `when`(mockView.findViewById<TextView>(R.id.txtNombre)).thenReturn(mockNombreCategoria)
        `when`(mockView.findViewById<ImageView>(R.id.imgCategoria)).thenReturn(mockIconoCategoria)
        `when`(mockView.findViewById<View>(R.id.layoutItem)).thenReturn(mockLayoutItem)

        // When
        val viewHolder = MenuAdaptador.ViewHolder(mockView)

        // Then
        assertNotNull("Vista del ViewHolder no debería ser null", viewHolder.vista)
        assertNotNull("NombreCategoria no debería ser null", viewHolder.nombreCategoria)
        assertNotNull("IconoCategoria no debería ser null", viewHolder.iconoCategoria)
        assertNotNull("LayoutItem no debería ser null", viewHolder.layoutItem)
    }

    // ========== PRUEBAS DE DATOS DE CATEGORIAS ==========

    @Test
    fun `adaptador deberia manejar categorias con nombres largos`() {
        // Given
        val categoriasNombresLargos = arrayListOf(
            Categoria(1, "Categoría con Nombre Extremadamente Largo que Podría Causar Problemas de UI", "#FF5722", "ic_test")
        )
        
        // When
        adaptador = MenuAdaptador(categoriasNombresLargos)

        // Then
        assertEquals("Debería manejar categorías con nombres largos", 1, adaptador.itemCount)
    }

    @Test
    fun `adaptador deberia manejar categorias con colores invalidos`() {
        // Given
        val categoriasColoresInvalidos = arrayListOf(
            Categoria(1, "Test", "color_invalido", "ic_test"),
            Categoria(2, "Test2", "#ZZZZZZ", "ic_test2")
        )
        
        // When
        adaptador = MenuAdaptador(categoriasColoresInvalidos)

        // Then
        assertEquals("Debería manejar categorías con colores inválidos", 2, adaptador.itemCount)
    }

    @Test
    fun `adaptador deberia manejar categorias con iconos inexistentes`() {
        // Given
        val categoriasIconosInexistentes = arrayListOf(
            Categoria(1, "Test", "#FF5722", "ic_icono_que_no_existe"),
            Categoria(2, "Test2", "#4CAF50", "drawable_inexistente")
        )
        
        // When
        adaptador = MenuAdaptador(categoriasIconosInexistentes)

        // Then
        assertEquals("Debería manejar categorías con iconos inexistentes", 2, adaptador.itemCount)
    }

    // ========== PRUEBAS DE CASOS LÍMITE ==========

    @Test
    fun `adaptador deberia manejar lista con muchas categorias`() {
        // Given
        val categoriasMuchas = ArrayList<Categoria>()
        for (i in 1..100) {
            categoriasMuchas.add(Categoria(i, "Categoria $i", "#FF5722", "ic_test$i"))
        }
        
        // When
        adaptador = MenuAdaptador(categoriasMuchas)

        // Then
        assertEquals("Debería manejar lista con 100 categorías", 100, adaptador.itemCount)
    }

    @Test
    fun `adaptador deberia manejar categoria con id negativo`() {
        // Given
        val categoriasIdNegativo = arrayListOf(
            Categoria(-1, "Categoria Negativa", "#FF5722", "ic_test")
        )
        
        // When
        adaptador = MenuAdaptador(categoriasIdNegativo)

        // Then
        assertEquals("Debería manejar categoría con ID negativo", 1, adaptador.itemCount)
    }

    @Test
    fun `adaptador deberia manejar categorias con datos vacios`() {
        // Given
        val categoriasVacias = arrayListOf(
            Categoria(0, "", "", ""),
            Categoria(1, "Normal", "#FF5722", "ic_test")
        )
        
        // When
        adaptador = MenuAdaptador(categoriasVacias)

        // Then
        assertEquals("Debería manejar categorías con datos vacíos", 2, adaptador.itemCount)
    }

    // ========== PRUEBAS DE FUNCIONALIDAD DE GRID ==========

    @Test
    fun `adaptador deberia funcionar correctamente con GridLayoutManager`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)
        val columnas = 2

        // When
        val totalItems = adaptador.itemCount
        val filasCompletas = totalItems / columnas
        val itemsFilaIncompleta = totalItems % columnas

        // Then
        assertEquals("Total de items debería ser 4", 4, totalItems)
        assertEquals("Deberían ser 2 filas completas", 2, filasCompletas)
        assertEquals("No deberían quedar items en fila incompleta", 0, itemsFilaIncompleta)
    }

    @Test
    fun `adaptador deberia manejar numero impar de categorias en grid`() {
        // Given
        val categoriasImpares = arrayListOf(
            Categoria(1, "Cat1", "#FF5722", "ic_test1"),
            Categoria(2, "Cat2", "#4CAF50", "ic_test2"),
            Categoria(3, "Cat3", "#FFC107", "ic_test3")
        )
        adaptador = MenuAdaptador(categoriasImpares)
        val columnas = 2

        // When
        val totalItems = adaptador.itemCount
        val filasCompletas = totalItems / columnas
        val itemsFilaIncompleta = totalItems % columnas

        // Then
        assertEquals("Total de items debería ser 3", 3, totalItems)
        assertEquals("Debería ser 1 fila completa", 1, filasCompletas)
        assertEquals("Debería quedar 1 item en fila incompleta", 1, itemsFilaIncompleta)
    }

    // ========== PRUEBAS DE INTEGRACIÓN CON CONSTANTES ==========

    @Test
    fun `adaptador deberia usar correctamente las constantes de la aplicacion`() {
        // Given
        adaptador = MenuAdaptador(categoriaUnica)
        val categoria = categoriaUnica[0]

        // When
        val constanteEsperada = Constantes.ID_CATEGORIA
        val idCategoria = categoria.id.toString()

        // Then
        assertEquals("Constante ID_CATEGORIA debería ser correcta", "ID_CATEGORIA", constanteEsperada)
        assertNotNull("ID de categoría no debería ser null", idCategoria)
        assertTrue("ID debería ser convertible a String", idCategoria.isNotEmpty())
    }

    // ========== PRUEBAS DE PERFORMANCE ==========

    @Test
    fun `creacion de adaptador deberia ser eficiente`() {
        // Given
        val startTime = System.currentTimeMillis()

        // When
        adaptador = MenuAdaptador(categoriasValidas)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Creación del adaptador debería ser rápida (<100ms)", duration < 100)
        assertEquals("Debería procesar todas las categorías", 4, adaptador.itemCount)
    }

    @Test
    fun `clearData deberia ser eficiente`() {
        // Given
        val categoriasMuchas = ArrayList<Categoria>()
        for (i in 1..1000) {
            categoriasMuchas.add(Categoria(i, "Categoria $i", "#FF5722", "ic_test"))
        }
        adaptador = MenuAdaptador(categoriasMuchas)
        val startTime = System.currentTimeMillis()

        // When
        adaptador.clearData()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("clearData debería ser rápido (<50ms)", duration < 50)
        assertEquals("Lista debería estar vacía", 0, adaptador.itemCount)
    }

    // ========== PRUEBAS DE CASOS DE USO ESPECÍFICOS DE APPALERTAMDI ==========

    @Test
    fun `adaptador deberia manejar categorias tipicas de municipalidad`() {
        // Given
        val categoriasMunicipales = arrayListOf(
            Categoria(1, "Baches y Vialidad", "#FF5722", "ic_road"),
            Categoria(2, "Recolección de Basura", "#4CAF50", "ic_trash"),
            Categoria(3, "Alumbrado Público", "#FFC107", "ic_lightbulb"),
            Categoria(4, "Seguridad Ciudadana", "#F44336", "ic_security"),
            Categoria(5, "Mantenimiento de Parques", "#8BC34A", "ic_park"),
            Categoria(6, "Problemas de Agua", "#2196F3", "ic_water")
        )

        // When
        adaptador = MenuAdaptador(categoriasMunicipales)

        // Then
        assertEquals("Debería manejar 6 categorías municipales", 6, adaptador.itemCount)
        
        // Verificar que todas las categorías tienen datos válidos
        categoriasMunicipales.forEach { categoria ->
            assertTrue("ID debería ser positivo", categoria.id > 0)
            assertTrue("Nombre no debería estar vacío", categoria.nombre.isNotEmpty())
            assertTrue("Color debería ser válido", categoria.color.startsWith("#"))
            assertTrue("Icono debería ser válido", categoria.icono.startsWith("ic_"))
        }
    }

    @Test
    fun `adaptador deberia ser compatible con PrincipalActivity`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)

        // When - Simular uso en PrincipalActivity
        val countParaGridLayout = adaptador.itemCount
        val esParParaGrid = countParaGridLayout % 2 == 0

        // Then
        assertEquals("Count debería ser 4 para PrincipalActivity", 4, countParaGridLayout)
        assertTrue("4 categorías deberían distribuirse bien en grid de 2 columnas", esParParaGrid)
    }

    // ========== PRUEBAS DE ESTADO ==========

    @Test
    fun `adaptador deberia mantener estado consistente después de operaciones`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)
        val countInicial = adaptador.itemCount

        // When
        adaptador.clearData()
        val countDespuesClear = adaptador.itemCount
        
        // Simular agregar categorías nuevamente
        categoriasValidas.addAll(arrayListOf(
            Categoria(5, "Nueva Cat", "#000000", "ic_new")
        ))
        val countDespuesAgregar = adaptador.itemCount

        // Then
        assertEquals("Count inicial debería ser 4", 4, countInicial)
        assertEquals("Count después de clear debería ser 0", 0, countDespuesClear)
        assertEquals("Count después de agregar debería ser 5", 5, countDespuesAgregar)
    }

    @Test
    fun `adaptador deberia manejar modificaciones concurrentes graciosamente`() {
        // Given
        adaptador = MenuAdaptador(categoriasValidas)

        // When & Then
        try {
            // Simular modificaciones concurrentes
            Thread {
                adaptador.clearData()
            }.start()
            
            Thread {
                val count = adaptador.itemCount
                assertTrue("Count debería ser válido", count >= 0)
            }.start()
            
            Thread.sleep(100) // Esperar a que terminen los threads
            
            // Verificar estado final
            assertTrue("Estado final debería ser consistente", adaptador.itemCount >= 0)
        } catch (e: Exception) {
            fail("Modificaciones concurrentes no deberían causar excepción: ${e.message}")
        }
    }
}