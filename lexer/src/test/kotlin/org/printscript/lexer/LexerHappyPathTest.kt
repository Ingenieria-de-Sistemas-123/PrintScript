package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.token.TokenType

class LexerHappyPathTest {

    @Test
    fun `tokeniza declaracion y println - retorna tipos en orden y finaliza en EOF`() {
        val code = """
            let name: string = "Joe";
            println(name);
        """.trimIndent()

        val tokens = Lexer().lex(code)
        val tiposEsperados = listOf(
            TokenType.LET,
            TokenType.IDENTIFIER,
            TokenType.COLON,
            TokenType.STRING_TYPE,
            TokenType.EQUAL,
            TokenType.STRING,
            TokenType.SEMICOLON,

            TokenType.PRINTLN,
            TokenType.OPEN_PAREN,
            TokenType.IDENTIFIER,
            TokenType.CLOSE_PAREN,
            TokenType.SEMICOLON,
            TokenType.EOF
        )

        assertEquals(tiposEsperados, tokens.map { it.type })

        // Valores clave (asegura que se recortan comillas)
        assertEquals("let", tokens[0].value)
        assertEquals("name", tokens[1].value)
        assertEquals("string", tokens[3].value)
        assertEquals("Joe", tokens[5].value)
    }
}
