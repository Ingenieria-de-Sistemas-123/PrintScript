package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.printscript.lexer.Lexer
import kotlin.test.Test

class ParserErrorTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test
    fun `declaracion sin punto y coma`() {
        val code = """let x: number = 1"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }

        assertTrue(ex.message?.contains("Se esperaba ';'") == true)
    }

    @Test
    fun `asignacion sin punto y coma`() {
        val code = """x = 5"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("Se esperaba ';' al final de la asignación") == true)
    }

    @Test
    fun `println con parentesis de cierre faltante`() {
        val code = """println(1 + 2;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("Se esperaba ')'") == true)
    }

    @Test
    fun `expresion con parentesis anidados sin cierre`() {
        val code = """x = (1 + (2 * 3);"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("Se esperaba ')'") == true)
    }

    @Test
    fun `identificador inicial sin '=' no es sentencia valida`() {
        val code = """foo;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("asignación o declaración") == true)
    }

    @Test
    fun `tipo invalido en declaracion`() {
        val code = """let flag: boolean;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        // Lo lanza VariableDeclarationBuilder cuando el tipo no es number|string
        assertTrue(ex.message?.contains("Se esperaba el tipo (number|string)") == true)
    }

    @Test
    fun `token invalido al inicio de sentencia`() {
        val code = """*;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("Token inesperado en inicio de sentencia") == true)
    }

    @Test
    fun `identificador seguido de literal sin '='`() {
        val code = """x 5;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        // DefaultParser detecta IDENTIFIER que no tiene '=' luego
        assertTrue(ex.message?.contains("asignación o declaración") == true)
    }

    @Test
    fun `println sin parentesis de apertura`() {
        val code = """println 123;"""
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        // PrintBuilder espera '(' inmediatamente
        assertTrue(ex.message?.contains("Se esperaba '('") == true)
    }

    @Test
    fun `reporta linea y columna en error (smoke)`() {
        // Ubicamos el error en la segunda línea, columna 1 (aprox según tu lexer)
        val code =
            """
            let x: number = 1;
            println(1 + 2;
            """.trimIndent()

        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        // No todos los lexers cuentan columna igual, pero al menos la línea debería ser 2
        assertEquals(2, ex.line, "La línea del error debería ser la segunda")
        // La columna puede variar; validamos que sea >=1
        assertTrue(ex.column >= 1, "La columna debería ser >= 1")
    }

    @Test
    fun `cierre de parentesis suelto al inicio`() {
        val code =
            """
            );
            """.trimIndent()
        val ex =
            assertThrows<ParseException> {
                parser.parse(lexer.lex(code))
            }
        assertTrue(ex.message?.contains("Token inesperado en inicio de sentencia") == true)
    }
}
