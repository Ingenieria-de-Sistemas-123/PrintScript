package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.lexer.exception.LexicalException

class LexerErrorsTest {
    @Test
    fun `caracter inesperado arroba - lanza LexicalException con linea y columna`() {
        val code = "@"
        assertThrows(LexicalException::class.java) { Lexer().lex(code) }
    }

    @Test
    fun `caracter inesperado despues de programa valido - error al encontrar basura tras ultima sentencia`() {
        val code =
            """
            let x:number=1;
            println(x); $$$
            """.trimIndent()

        assertThrows(LexicalException::class.java) { Lexer().lex(code) }
    }
}
