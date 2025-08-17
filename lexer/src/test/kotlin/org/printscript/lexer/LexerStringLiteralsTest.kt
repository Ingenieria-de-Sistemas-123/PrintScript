package org.printscript.lexer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.printscript.lexer.exception.LexicalException
import org.printscript.token.TokenType

class LexerStringLiteralsTest {

    @Test
    fun `string con comillas dobles - recorta comillas y conserva contenido`() {
        val code = """"hola""""
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.STRING, tokens[0].type)
        assertEquals("hola", tokens[0].value)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun `string con comillas simples - recorta comillas y conserva contenido`() {
        val code = "'hola'"
        val tokens = Lexer().lex(code)

        assertEquals(TokenType.STRING, tokens[0].type)
        assertEquals("hola", tokens[0].value)
        assertEquals(TokenType.EOF, tokens.last().type)
    }

    @Test
    fun `string NO admite salto de linea real - lanza LexicalException con posicion`() {
        val code = "\"hola\nmundo\""
        val ex = assertThrows(LexicalException::class.java) { Lexer().lex(code) }

        assertTrue(ex.message!!.contains("Caracter inesperado"))
        assertTrue(ex.line >= 1)
        assertTrue(ex.column >= 1)
    }
}
