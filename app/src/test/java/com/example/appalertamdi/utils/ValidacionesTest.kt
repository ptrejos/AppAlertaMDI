package com.example.appalertamdi.utils

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File
import java.util.regex.Pattern

/**
 * Pruebas unitarias para las validaciones de AppAlertaMDI
 * 
 * Ubicación: app/src/test/java/com/example/appalertamdi/utils/ValidacionesTest.kt
 * 
 * Ejecutar con: ./gradlew test
 * 
 * Estas pruebas validan la lógica de validación de datos utilizada en
 * la aplicación para reportes ciudadanos de la Municipalidad de Independencia
 */
class ValidacionesTest {

    private lateinit var validaciones: Validaciones

    @Before
    fun setUp() {
        validaciones = Validaciones()
    }

    // ========== PRUEBAS DE VALIDACIÓN DE USUARIO ==========

    @Test
    fun `validarUsuario deberia retornar true para usuario valido`() {
        // Given
        val usuarioValido = "juan.perez"

        // When
        val resultado = validaciones.validarUsuario(usuarioValido)

        // Then
        assertTrue("Usuario válido debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarUsuario deberia retornar false para usuario vacio`() {
        // Given
        val usuarioVacio = ""

        // When
        val resultado = validaciones.validarUsuario(usuarioVacio)

        // Then
        assertFalse("Usuario vacío no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "El nombre de usuario no puede estar vacío", resultado.mensaje)
    }

    @Test
    fun `validarUsuario deberia retornar false para usuario con espacios`() {
        // Given
        val usuarioConEspacios = "juan perez"

        // When
        val resultado = validaciones.validarUsuario(usuarioConEspacios)

        // Then
        assertFalse("Usuario con espacios no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar espacios", 
                    "El nombre de usuario no puede contener espacios", resultado.mensaje)
    }

    @Test
    fun `validarUsuario deberia retornar false para usuario muy corto`() {
        // Given
        val usuarioCorto = "ab"

        // When
        val resultado = validaciones.validarUsuario(usuarioCorto)

        // Then
        assertFalse("Usuario muy corto no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud mínima", 
                    "El nombre de usuario debe tener al menos 3 caracteres", resultado.mensaje)
    }

    @Test
    fun `validarUsuario deberia retornar false para usuario muy largo`() {
        // Given
        val usuarioLargo = "a".repeat(51) // 51 caracteres

        // When
        val resultado = validaciones.validarUsuario(usuarioLargo)

        // Then
        assertFalse("Usuario muy largo no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud máxima", 
                    "El nombre de usuario no puede tener más de 50 caracteres", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE CONTRASEÑA ==========

    @Test
    fun `validarPassword deberia retornar true para password valido`() {
        // Given
        val passwordValido = "MiPassword123"

        // When
        val resultado = validaciones.validarPassword(passwordValido)

        // Then
        assertTrue("Password válido debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarPassword deberia retornar false para password vacio`() {
        // Given
        val passwordVacio = ""

        // When
        val resultado = validaciones.validarPassword(passwordVacio)

        // Then
        assertFalse("Password vacío no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "La contraseña no puede estar vacía", resultado.mensaje)
    }

    @Test
    fun `validarPassword deberia retornar false para password muy corto`() {
        // Given
        val passwordCorto = "12345"

        // When
        val resultado = validaciones.validarPassword(passwordCorto)

        // Then
        assertFalse("Password muy corto no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud mínima", 
                    "La contraseña debe tener al menos 6 caracteres", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE DESCRIPCIÓN ==========

    @Test
    fun `validarDescripcion deberia retornar true para descripcion valida`() {
        // Given
        val descripcionValida = "Bache en la Av. Túpac Amaru, altura del cruce con Jr. Huanta"

        // When
        val resultado = validaciones.validarDescripcion(descripcionValida)

        // Then
        assertTrue("Descripción válida debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarDescripcion deberia retornar false para descripcion vacia`() {
        // Given
        val descripcionVacia = ""

        // When
        val resultado = validaciones.validarDescripcion(descripcionVacia)

        // Then
        assertFalse("Descripción vacía no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "Ingrese una descripción", resultado.mensaje)
    }

    @Test
    fun `validarDescripcion deberia retornar false para descripcion muy corta`() {
        // Given
        val descripcionCorta = "Bache"

        // When
        val resultado = validaciones.validarDescripcion(descripcionCorta)

        // Then
        assertFalse("Descripción muy corta no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud mínima", 
                    "La descripción debe tener al menos 10 caracteres", resultado.mensaje)
    }

    @Test
    fun `validarDescripcion deberia retornar false para descripcion muy larga`() {
        // Given
        val descripcionLarga = "A".repeat(501) // 501 caracteres

        // When
        val resultado = validaciones.validarDescripcion(descripcionLarga)

        // Then
        assertFalse("Descripción muy larga no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud máxima", 
                    "La descripción no puede tener más de 500 caracteres", resultado.mensaje)
    }

    @Test
    fun `validarDescripcion deberia retornar false para descripcion solo con espacios`() {
        // Given
        val descripcionEspacios = "     "

        // When
        val resultado = validaciones.validarDescripcion(descripcionEspacios)

        // Then
        assertFalse("Descripción con solo espacios no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "Ingrese una descripción", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE COORDENADAS ==========

    @Test
    fun `validarCoordenadas deberia retornar true para coordenadas validas de Lima`() {
        // Given
        val latitudLima = "-12.0464"
        val longitudLima = "-77.0428"

        // When
        val resultado = validaciones.validarCoordenadas(latitudLima, longitudLima)

        // Then
        assertTrue("Coordenadas válidas de Lima deberían pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarCoordenadas deberia retornar false para latitud vacia`() {
        // Given
        val latitudVacia = ""
        val longitudValida = "-77.0428"

        // When
        val resultado = validaciones.validarCoordenadas(latitudVacia, longitudValida)

        // Then
        assertFalse("Latitud vacía no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar ubicación", 
                    "No se pudo obtener la ubicación", resultado.mensaje)
    }

    @Test
    fun `validarCoordenadas deberia retornar false para longitud vacia`() {
        // Given
        val latitudValida = "-12.0464"
        val longitudVacia = ""

        // When
        val resultado = validaciones.validarCoordenadas(latitudValida, longitudVacia)

        // Then
        assertFalse("Longitud vacía no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar ubicación", 
                    "No se pudo obtener la ubicación", resultado.mensaje)
    }

    @Test
    fun `validarCoordenadas deberia retornar false para coordenadas fuera de rango`() {
        // Given
        val latitudInvalida = "100.0" // Fuera del rango -90 a 90
        val longitudInvalida = "200.0" // Fuera del rango -180 a 180

        // When
        val resultado = validaciones.validarCoordenadas(latitudInvalida, longitudInvalida)

        // Then
        assertFalse("Coordenadas fuera de rango no deberían pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar coordenadas inválidas", 
                    "Las coordenadas no son válidas", resultado.mensaje)
    }

    @Test
    fun `validarCoordenadas deberia retornar false para coordenadas no numericas`() {
        // Given
        val latitudTexto = "abc"
        val longitudTexto = "xyz"

        // When
        val resultado = validaciones.validarCoordenadas(latitudTexto, longitudTexto)

        // Then
        assertFalse("Coordenadas no numéricas no deberían pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar formato inválido", 
                    "El formato de las coordenadas no es válido", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE CATEGORÍA ==========

    @Test
    fun `validarCategoria deberia retornar true para categoria valida`() {
        // Given
        val categoriaValida = "1"

        // When
        val resultado = validaciones.validarCategoria(categoriaValida)

        // Then
        assertTrue("Categoría válida debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarCategoria deberia retornar false para categoria vacia`() {
        // Given
        val categoriaVacia = ""

        // When
        val resultado = validaciones.validarCategoria(categoriaVacia)

        // Then
        assertFalse("Categoría vacía no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "Seleccione una categoría", resultado.mensaje)
    }

    @Test
    fun `validarCategoria deberia retornar false para categoria null`() {
        // Given
        val categoriaNula = "null"

        // When
        val resultado = validaciones.validarCategoria(categoriaNula)

        // Then
        assertFalse("Categoría null no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "Seleccione una categoría", resultado.mensaje)
    }

    @Test
    fun `validarCategoria deberia retornar false para categoria no numerica`() {
        // Given
        val categoriaTexto = "abc"

        // When
        val resultado = validaciones.validarCategoria(categoriaTexto)

        // Then
        assertFalse("Categoría no numérica no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar formato", 
                    "El ID de categoría debe ser numérico", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE ARCHIVO DE FOTO ==========

    @Test
    fun `validarArchivoFoto deberia retornar false para archivo null`() {
        // Given
        val archivoNull: File? = null

        // When
        val resultado = validaciones.validarArchivoFoto(archivoNull)

        // Then
        assertFalse("Archivo null no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar foto requerida", 
                    "Debe tomar una foto de la incidencia", resultado.mensaje)
    }

    @Test
    fun `validarArchivoFoto deberia retornar false para archivo que no existe`() {
        // Given
        val archivoInexistente = File("/ruta/inexistente/foto.jpg")

        // When
        val resultado = validaciones.validarArchivoFoto(archivoInexistente)

        // Then
        assertFalse("Archivo inexistente no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar archivo inválido", 
                    "El archivo de foto no es válido", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE EMAIL ==========

    @Test
    fun `validarEmail deberia retornar true para email valido`() {
        // Given
        val emailValido = "ciudadano@gmail.com"

        // When
        val resultado = validaciones.validarEmail(emailValido)

        // Then
        assertTrue("Email válido debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarEmail deberia retornar false para email invalido`() {
        // Given
        val emailInvalido = "email_invalido"

        // When
        val resultado = validaciones.validarEmail(emailInvalido)

        // Then
        assertFalse("Email inválido no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar formato", 
                    "El formato del email no es válido", resultado.mensaje)
    }

    @Test
    fun `validarEmail deberia retornar false para email vacio`() {
        // Given
        val emailVacio = ""

        // When
        val resultado = validaciones.validarEmail(emailVacio)

        // Then
        assertFalse("Email vacío no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería ser específico", 
                    "El email no puede estar vacío", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN DE TELÉFONO ==========

    @Test
    fun `validarTelefono deberia retornar true para telefono peruano valido`() {
        // Given
        val telefonoValido = "987654321"

        // When
        val resultado = validaciones.validarTelefono(telefonoValido)

        // Then
        assertTrue("Teléfono peruano válido debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarTelefono deberia retornar false para telefono muy corto`() {
        // Given
        val telefonoCorto = "12345"

        // When
        val resultado = validaciones.validarTelefono(telefonoCorto)

        // Then
        assertFalse("Teléfono muy corto no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar longitud", 
                    "El teléfono debe tener 9 dígitos", resultado.mensaje)
    }

    @Test
    fun `validarTelefono deberia retornar false para telefono con letras`() {
        // Given
        val telefonoConLetras = "98765432a"

        // When
        val resultado = validaciones.validarTelefono(telefonoConLetras)

        // Then
        assertFalse("Teléfono con letras no debería pasar la validación", resultado.esValido)
        assertEquals("Mensaje de error debería mencionar solo números", 
                    "El teléfono debe contener solo números", resultado.mensaje)
    }

    // ========== PRUEBAS DE VALIDACIÓN COMPUESTA ==========

    @Test
    fun `validarFormularioCompleto deberia retornar true para todos los campos validos`() {
        // Given
        val datosValidos = DatosFormulario(
            usuario = "juan.perez",
            password = "miPassword123",
            descripcion = "Bache en la Av. Túpac Amaru que necesita reparación urgente",
            latitud = "-12.0464",
            longitud = "-77.0428",
            categoria = "1",
            email = "juan.perez@gmail.com",
            telefono = "987654321"
        )

        // When
        val resultado = validaciones.validarFormularioCompleto(datosValidos)

        // Then
        assertTrue("Formulario con datos válidos debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería estar vacío", resultado.mensaje.isEmpty())
    }

    @Test
    fun `validarFormularioCompleto deberia retornar false si algun campo es invalido`() {
        // Given
        val datosConErrores = DatosFormulario(
            usuario = "", // Usuario vacío - ERROR
            password = "miPassword123",
            descripcion = "Bache en la Av. Túpac Amaru que necesita reparación urgente",
            latitud = "-12.0464",
            longitud = "-77.0428",
            categoria = "1",
            email = "juan.perez@gmail.com",
            telefono = "987654321"
        )

        // When
        val resultado = validaciones.validarFormularioCompleto(datosConErrores)

        // Then
        assertFalse("Formulario con errores no debería pasar la validación", resultado.esValido)
        assertTrue("Mensaje de error debería contener detalles", resultado.mensaje.isNotEmpty())
    }

    // ========== PRUEBAS DE CASOS LÍMITE ==========

    @Test
    fun `validaciones deberian manejar caracteres especiales en descripcion`() {
        // Given
        val descripcionConEspeciales = "Bache en la Av. 28 de Julio #123, señalización dañada (50% deterioro)"

        // When
        val resultado = validaciones.validarDescripcion(descripcionConEspeciales)

        // Then
        assertTrue("Descripción con caracteres especiales válidos debería pasar", resultado.esValido)
    }

    @Test
    fun `validaciones deberian rechazar coordenadas de fuera del Peru`() {
        // Given
        val latitudBrasil = "-15.7942" // Brasília
        val longitudBrasil = "-47.8822"

        // When
        val resultado = validaciones.validarCoordenadasPeru(latitudBrasil, longitudBrasil)

        // Then
        assertFalse("Coordenadas fuera del Perú no deberían pasar la validación", resultado.esValido)
        assertEquals("Mensaje debería mencionar ubicación fuera del país", 
                    "La ubicación debe estar dentro del territorio peruano", resultado.mensaje)
    }

    @Test
    fun `validaciones deberian aceptar coordenadas validas del Peru`() {
        // Given
        val latitudLima = "-12.0464"
        val longitudLima = "-77.0428"

        // When
        val resultado = validaciones.validarCoordenadasPeru(latitudLima, longitudLima)

        // Then
        assertTrue("Coordenadas del Perú deberían pasar la validación", resultado.esValido)
    }

    // ========== PRUEBAS DE PERFORMANCE ==========

    @Test
    fun `validaciones deberian ser eficientes para operaciones masivas`() {
        // Given
        val startTime = System.currentTimeMillis()
        val datosValidos = DatosFormulario(
            usuario = "test.user",
            password = "testPass123",
            descripcion = "Descripción de prueba para medir performance de validaciones",
            latitud = "-12.0464",
            longitud = "-77.0428",
            categoria = "1",
            email = "test@test.com",
            telefono = "987654321"
        )

        // When
        repeat(1000) {
            validaciones.validarFormularioCompleto(datosValidos)
        }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("1000 validaciones deberían ejecutarse en menos de 1 segundo", duration < 1000)
    }

    // ========== CLASES DE APOYO ==========

    data class DatosFormulario(
        val usuario: String,
        val password: String,
        val descripcion: String,
        val latitud: String,
        val longitud: String,
        val categoria: String,
        val email: String,
        val telefono: String
    )

    data class ResultadoValidacion(
        val esValido: Boolean,
        val mensaje: String = ""
    )

    // ========== CLASE VALIDACIONES (IMPLEMENTACIÓN DE EJEMPLO) ==========

    class Validaciones {
        
        fun validarUsuario(usuario: String): ResultadoValidacion {
            when {
                usuario.isEmpty() -> return ResultadoValidacion(false, "El nombre de usuario no puede estar vacío")
                usuario.contains(" ") -> return ResultadoValidacion(false, "El nombre de usuario no puede contener espacios")
                usuario.length < 3 -> return ResultadoValidacion(false, "El nombre de usuario debe tener al menos 3 caracteres")
                usuario.length > 50 -> return ResultadoValidacion(false, "El nombre de usuario no puede tener más de 50 caracteres")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarPassword(password: String): ResultadoValidacion {
            when {
                password.isEmpty() -> return ResultadoValidacion(false, "La contraseña no puede estar vacía")
                password.length < 6 -> return ResultadoValidacion(false, "La contraseña debe tener al menos 6 caracteres")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarDescripcion(descripcion: String): ResultadoValidacion {
            val descripcionLimpia = descripcion.trim()
            when {
                descripcionLimpia.isEmpty() -> return ResultadoValidacion(false, "Ingrese una descripción")
                descripcionLimpia.length < 10 -> return ResultadoValidacion(false, "La descripción debe tener al menos 10 caracteres")
                descripcionLimpia.length > 500 -> return ResultadoValidacion(false, "La descripción no puede tener más de 500 caracteres")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarCoordenadas(latitud: String, longitud: String): ResultadoValidacion {
            when {
                latitud.isEmpty() || longitud.isEmpty() -> return ResultadoValidacion(false, "No se pudo obtener la ubicación")
                else -> {
                    try {
                        val lat = latitud.toDouble()
                        val lng = longitud.toDouble()
                        when {
                            lat < -90 || lat > 90 || lng < -180 || lng > 180 -> 
                                return ResultadoValidacion(false, "Las coordenadas no son válidas")
                            else -> return ResultadoValidacion(true)
                        }
                    } catch (e: NumberFormatException) {
                        return ResultadoValidacion(false, "El formato de las coordenadas no es válido")
                    }
                }
            }
        }

        fun validarCategoria(categoria: String): ResultadoValidacion {
            when {
                categoria.isEmpty() || categoria == "null" -> return ResultadoValidacion(false, "Seleccione una categoría")
                else -> {
                    try {
                        categoria.toInt()
                        return ResultadoValidacion(true)
                    } catch (e: NumberFormatException) {
                        return ResultadoValidacion(false, "El ID de categoría debe ser numérico")
                    }
                }
            }
        }

        fun validarArchivoFoto(archivo: File?): ResultadoValidacion {
            when {
                archivo == null -> return ResultadoValidacion(false, "Debe tomar una foto de la incidencia")
                !archivo.exists() -> return ResultadoValidacion(false, "El archivo de foto no es válido")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarEmail(email: String): ResultadoValidacion {
            when {
                email.isEmpty() -> return ResultadoValidacion(false, "El email no puede estar vacío")
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                    return ResultadoValidacion(false, "El formato del email no es válido")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarTelefono(telefono: String): ResultadoValidacion {
            when {
                telefono.length != 9 -> return ResultadoValidacion(false, "El teléfono debe tener 9 dígitos")
                !telefono.all { it.isDigit() } -> return ResultadoValidacion(false, "El teléfono debe contener solo números")
                else -> return ResultadoValidacion(true)
            }
        }

        fun validarCoordenadasPeru(latitud: String, longitud: String): ResultadoValidacion {
            try {
                val lat = latitud.toDouble()
                val lng = longitud.toDouble()
                
                // Coordenadas aproximadas del Perú
                val latMinPeru = -18.5
                val latMaxPeru = -0.5
                val lngMinPeru = -81.5
                val lngMaxPeru = -68.5
                
                when {
                    lat < latMinPeru || lat > latMaxPeru || lng < lngMinPeru || lng > lngMaxPeru -> 
                        return ResultadoValidacion(false, "La ubicación debe estar dentro del territorio peruano")
                    else -> return ResultadoValidacion(true)
                }
            } catch (e: NumberFormatException) {
                return ResultadoValidacion(false, "El formato de las coordenadas no es válido")
            }
        }

        fun validarFormularioCompleto(datos: DatosFormulario): ResultadoValidacion {
            val errores = mutableListOf<String>()
            
            validarUsuario(datos.usuario).let { if (!it.esValido) errores.add(it.mensaje) }
            validarPassword(datos.password).let { if (!it.esValido) errores.add(it.mensaje) }
            validarDescripcion(datos.descripcion).let { if (!it.esValido) errores.add(it.mensaje) }
            validarCoordenadas(datos.latitud, datos.longitud).let { if (!it.esValido) errores.add(it.mensaje) }
            validarCategoria(datos.categoria).let { if (!it.esValido) errores.add(it.mensaje) }
            validarEmail(datos.email).let { if (!it.esValido) errores.add(it.mensaje) }
            validarTelefono(datos.telefono).let { if (!it.esValido) errores.add(it.mensaje) }
            
            return if (errores.isEmpty()) {
                ResultadoValidacion(true)
            } else {
                ResultadoValidacion(false, errores.joinToString("; "))
            }
        }
    }
}