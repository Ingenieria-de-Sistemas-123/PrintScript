package org.printscript.parser.helpers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.parser.ParseException
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.TokenType

class TokenHandlerTest {
    @Test
    fun `advance y current se mueven hasta EOF`() {
        val h = TokenHandler(listOf(t(TokenType.NUMBER, "42", 1, 1), eof(1, 3)))
        assertEquals(TokenType.NUMBER, h.current().type)
        h.advance()
        assertEquals(TokenType.EOF, h.current().type)
        // en EOF, advance no incrementa pos (contrato implementado)
        val posAntes = h.pos
        h.advance()
        assertEquals(posAntes, h.pos)
    }

    @Test
    fun `match devuelve true si coincide y avanza`() {
        val h = TokenHandler(listOf(t(TokenType.LET, "let"), eof()))
        assertTrue(h.match(TokenType.LET))
        assertEquals(TokenType.EOF, h.current().type)
    }

    @Test
    fun `match devuelve false si no coincide y no avanza`() {
        val h = TokenHandler(listOf(t(TokenType.LET, "let"), eof()))
        assertFalse(h.match(TokenType.NUMBER))
        assertEquals(TokenType.LET, h.current().type)
    }

    @Test
    fun `expect lanza ParseException con linea y columna correctas`() {
        val h = TokenHandler(listOf(t(TokenType.NUMBER, "7", line = 5, col = 9), eof()))
        val ex =
            assertThrows<ParseException> {
                h.expect(TokenType.LET, "Se esperaba let")
            }
        assertEquals(5, ex.line)
        assertEquals(9, ex.column)
        assertTrue(ex.message!!.contains("Se esperaba let"))
        assertTrue(ex.message!!.contains("Encontré NUMBER '7'"))
    }
}
