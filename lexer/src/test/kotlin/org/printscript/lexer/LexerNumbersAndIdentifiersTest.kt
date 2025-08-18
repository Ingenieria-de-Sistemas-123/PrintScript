package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.token.TokenType

class LexerNumbersAndIdentifiersTest {

    @Test
    fun `tokeniza numeros - entero y decimal conservan valor textual`() {
        val code = "3 3.14;"
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("3", tokens[0].value)

        assertEquals(TokenType.NUMBER, tokens[1].type)
        assertEquals("3.14", tokens[1].value)

        assertEquals(TokenType.SEMICOLON, tokens[2].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun `identificador con underscore y digitos - matchea segun regex`() {
        val code = "my_var1 another2 _ok3;"
        val tokens = Lexer().lex(code)

        assertEquals(listOf("my_var1", "another2", "_ok3"), tokens.take(3).map { it.value })
        assertEquals(TokenType.SEMICOLON, tokens[3].type)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun `keyword let vs identifier lett - 'lett' se reconoce como IDENTIFIER por limite de palabra`() {
        val code = "lett"
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("lett", tokens[0].value)
        assertEquals(TokenType.EOF, tokens.last().type)
    }
}
