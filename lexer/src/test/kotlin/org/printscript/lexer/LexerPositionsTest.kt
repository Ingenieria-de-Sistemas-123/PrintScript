package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.token.TokenType

class LexerPositionsTest {

    @Test
    fun `posicion LET - comienza en linea 1 columna 1`() {
        val code = "let x: number = 5;"
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.LET, tokens[0].type)
        assertEquals(1, tokens[0].line)
        assertEquals(1, tokens[0].column)
    }

    @Test
    fun `posicion IDENTIFIER tras 'let ' - comienza en linea 1 columna 5`() {
        val code = "let x: number = 5;"
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.IDENTIFIER, tokens[1].type)
        assertEquals(1, tokens[1].line)
        assertEquals(5, tokens[1].column) // "let " ocupa 4 columnas
    }

    @Test
    fun `posicion PRINTLN en segunda linea - luego de salto de linea`() {
        val code = """
            let x: number = 5;
            println(x);
        """.trimIndent()

        val tokens = Lexer().lex(code)
        val idxPrintln = tokens.indexOfFirst { it.type == TokenType.PRINTLN }

        assertEquals(2, tokens[idxPrintln].line)
        assertEquals(1, tokens[idxPrintln].column)
    }

    @Test
    fun `manejo CRLF windows - incrementa linea solo con LF`() {
        val code = "let a:number=1;\r\nprintln(a);\r\n"
        val tokens = Lexer().lex(code)
        val idxPrintln = tokens.indexOfFirst { it.type == TokenType.PRINTLN }

        assertEquals(2, tokens[idxPrintln].line)
    }
}
