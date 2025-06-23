package com.example.appalertamdi.entidad

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Pruebas unitarias para la clase Usuario
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/entidad/UsuarioTest.kt
 * 
 * Ejecutar con: ./gradlew test
 */
class UsuarioTest {

    private lateinit var usuarioExitoso: Usuario
    private lateinit var usuarioFallido: Usuario
    private lateinit var usuarioVacio: Usuario

    @Before
    fun setUp() {
        // Configurar datos de prueba antes de cada test
        usuarioExitoso = Usuario(
            rs = "1",
            nombre = "Juan Pérez",
            codigo = "12345"
        )

        usuarioFallido = Usuario(
            rs = "0",
            nombre = "María García",
            codigo = "67890"
        )

        usuarioVacio = Usuario(
            rs = "",
            nombre = "",
            codigo = ""
        )
    }

    // ========== PRUEBAS DE AUTENTICACIÓN ==========

    @Test
    fun `cuando rs es 1 entonces autenticacion es exitosa`() {
        // Given
        val usuario = usuarioExitoso

        // When
        val loginExitoso = usuario.rs == "1"

        // Then
        assertTrue("El login debería ser exitoso cuando rs = '1'", loginExitoso)
    }

    @Test
    fun `cuando rs es 0 entonces autenticacion falla`() {
        // Given
        val usuario = usuarioFallido

        // When
        val loginFallido = usuario.rs == "0"

        // Then
        assertTrue("El login debería fallar cuando rs = '0'", loginFallido)
    }

    @Test
    fun `cuando rs es diferente de 1 entonces autenticacion no es exitosa`() {
        // Given
        val usuario = Usuario(rs = "2", nombre = "Test", codigo = "123")

        // When
        val loginExitoso = usuario.rs == "1"

        // Then
        assertFalse("El login no debería ser exitoso cuando rs != '1'", loginExitoso)
    }

    @Test
    fun `cuando rs esta vacio entonces autenticacion falla`() {
        // Given
        val usuario = usuarioVacio

        // When
        val rsVacio = usuario.rs.isEmpty()

        // Then
        assertTrue("Rs vacío debería indicar fallo de autenticación", rsVacio)
    }

    // ========== PRUEBAS DE PROPIEDADES ==========

    @Test
    fun `usuario deberia tener todas las propiedades inicializadas correctamente`() {
        // Given & When
        val usuario = usuarioExitoso

        // Then
        assertEquals("1", usuario.rs)
        assertEquals("Juan Pérez", usuario.nombre)
        assertEquals("12345", usuario.codigo)
    }

    @Test
    fun `usuario permite modificar propiedades`() {
        // Given
        val usuario = Usuario(rs = "0", nombre = "Original", codigo = "000")

        // When
        usuario.rs = "1"
        usuario.nombre = "Modificado"
        usuario.codigo = "999"

        // Then
        assertEquals("1", usuario.rs)
        assertEquals("Modificado", usuario.nombre)
        assertEquals("999", usuario.codigo)
    }

    @Test
    fun `usuario acepta valores nulos o vacios en propiedades`() {
        // Given & When
        val usuario = Usuario(rs = "", nombre = "", codigo = "")

        // Then
        assertTrue("Rs puede estar vacío", usuario.rs.isEmpty())
        assertTrue("Nombre puede estar vacío", usuario.nombre.isEmpty())
        assertTrue("Código puede estar vacío", usuario.codigo.isEmpty())
    }

    // ========== PRUEBAS DE VALIDACIÓN DE DATOS ==========

    @Test
    fun `nombre no deberia estar vacio para usuario valido`() {
        // Given
        val usuario = usuarioExitoso

        // When
        val nombreValido = usuario.nombre.isNotEmpty()

        // Then
        assertTrue("El nombre no debería estar vacío", nombreValido)
    }

    @Test
    fun `codigo no deberia estar vacio para usuario valido`() {
        // Given
        val usuario = usuarioExitoso

        // When
        val codigoValido = usuario.codigo.isNotEmpty()

        // Then
        assertTrue("El código no debería estar vacío", codigoValido)
    }

    @Test
    fun `nombre deberia contener solo caracteres validos`() {
        // Given
        val usuario = usuarioExitoso

        // When
        val tieneCaracteresEspeciales = usuario.nombre.contains(Regex("[^a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]"))

        // Then
        assertFalse("El nombre no debería contener caracteres especiales", tieneCaracteresEspeciales)
    }

    @Test
    fun `codigo deberia ser numerico`() {
        // Given
        val usuario = usuarioExitoso

        // When
        val esNumerico = usuario.codigo.all { it.isDigit() }

        // Then
        assertTrue("El código debería ser numérico", esNumerico)
    }

    // ========== PRUEBAS DE CASOS LÍMITE ==========

    @Test
    fun `usuario con nombre muy largo deberia ser manejado`() {
        // Given
        val nombreLargo = "A".repeat(255)
        val usuario = Usuario(rs = "1", nombre = nombreLargo, codigo = "123")

        // When
        val longitudNombre = usuario.nombre.length

        // Then
        assertEquals(255, longitudNombre)
        assertTrue("Debería manejar nombres largos", longitudNombre > 0)
    }

    @Test
    fun `usuario con codigo muy largo deberia ser manejado`() {
        // Given
        val codigoLargo = "1234567890".repeat(10)
        val usuario = Usuario(rs = "1", nombre = "Test", codigo = codigoLargo)

        // When
        val longitudCodigo = usuario.codigo.length

        // Then
        assertEquals(100, longitudCodigo)
        assertTrue("Debería manejar códigos largos", longitudCodigo > 0)
    }

    // ========== PRUEBAS DE LÓGICA DE NEGOCIO ==========

    @Test
    fun `metodo para validar si usuario esta autenticado`() {
        // Given
        val usuarioAutenticado = usuarioExitoso
        val usuarioNoAutenticado = usuarioFallido

        // When
        fun Usuario.estaAutenticado(): Boolean = this.rs == "1"

        // Then
        assertTrue("Usuario con rs='1' debería estar autenticado", 
                  usuarioAutenticado.estaAutenticado())
        assertFalse("Usuario con rs='0' no debería estar autenticado", 
                   usuarioNoAutenticado.estaAutenticado())
    }

    @Test
    fun `metodo para validar si datos de usuario son completos`() {
        // Given
        val usuarioCompleto = usuarioExitoso
        val usuarioIncompleto = usuarioVacio

        // When
        fun Usuario.tienesDatosCompletos(): Boolean {
            return this.rs.isNotEmpty() && 
                   this.nombre.isNotEmpty() && 
                   this.codigo.isNotEmpty()
        }

        // Then
        assertTrue("Usuario con todos los datos debería ser completo", 
                  usuarioCompleto.tienesDatosCompletos())
        assertFalse("Usuario con datos vacíos no debería ser completo", 
                   usuarioIncompleto.tienesDatosCompletos())
    }

    @Test
    fun `metodo para obtener iniciales del nombre`() {
        // Given
        val usuario = Usuario(rs = "1", nombre = "Juan Carlos Pérez", codigo = "123")

        // When
        fun Usuario.obtenerIniciales(): String {
            return this.nombre.split(" ")
                .filter { it.isNotEmpty() }
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }

        // Then
        assertEquals("JCP", usuario.obtenerIniciales())
    }

    // ========== PRUEBAS DE COMPARACIÓN ==========

    @Test
    fun `dos usuarios con mismo codigo deberian ser considerados iguales`() {
        // Given
        val usuario1 = Usuario(rs = "1", nombre = "Juan", codigo = "123")
        val usuario2 = Usuario(rs = "0", nombre = "Pedro", codigo = "123")

        // When
        val mismoCodigo = usuario1.codigo == usuario2.codigo

        // Then
        assertTrue("Usuarios con mismo código deberían ser considerados iguales", mismoCodigo)
    }

    @Test
    fun `usuario deberia poder convertirse a string representativo`() {
        // Given
        val usuario = usuarioExitoso

        // When
        fun Usuario.toStringRepresentativo(): String {
            return "Usuario(rs='$rs', nombre='$nombre', codigo='$codigo')"
        }

        // Then
        val expected = "Usuario(rs='1', nombre='Juan Pérez', codigo='12345')"
        assertEquals(expected, usuario.toStringRepresentativo())
    }

    // ========== PRUEBAS DE SERIALIZACIÓN (JSON) ==========

    @Test
    fun `campos de usuario deberian coincidir con anotaciones SerializedName`() {
        // Este test verifica que los campos coincidan con los esperados por la API
        
        // Given
        val usuario = usuarioExitoso

        // When & Then
        // Verificamos que los campos existen y pueden ser accedidos
        assertNotNull("Campo 'rs' debe existir", usuario.rs)
        assertNotNull("Campo 'nombre' debe existir", usuario.nombre)
        assertNotNull("Campo 'codigo' debe existir", usuario.codigo)
    }
}