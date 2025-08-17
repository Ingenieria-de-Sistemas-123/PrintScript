package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.token.TokenType

@Tag("smoke")
class LexerSmokeTest {

    @Test
    fun `programa basico - lexer produce tokens clave en orden y termina en EOF`() {
        val code = """
            let x: number = 5;
            println(x);
        """.trimIndent()

        val tokens = Lexer().lex(code)

        val tiposEsperados = listOf(
            TokenType.LET,
            TokenType.IDENTIFIER,
            TokenType.COLON,        // usa SEPARATOR si tu enum lo llama así
            TokenType.NUMBER_TYPE,
            TokenType.EQUAL,
            TokenType.NUMBER,
            TokenType.SEMICOLON,
            TokenType.PRINTLN,
            TokenType.OPEN_PAREN,
            TokenType.IDENTIFIER,
            TokenType.CLOSE_PAREN,
            TokenType.SEMICOLON,
            TokenType.EOF
        )

        assertEquals(tiposEsperados, tokens.map { it.type })
        assertEquals("x", tokens[1].value)     // sanity extra
        assertEquals("5", tokens[5].value)
    }
}
