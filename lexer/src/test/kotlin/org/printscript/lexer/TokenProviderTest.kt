package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.printscript.lexer.pattern.TokenProvider
import org.printscript.token.TokenType
import kotlin.test.Test

class TokenProviderTest {

    @Test
    fun getTokenFor_matchesAtGivenPosition_only() {
        val tp = TokenProvider.builder(
            mapOf("\\blet\\b" to TokenType.LET, "\\=" to TokenType.EQUAL)
        )
        val line = "  let x = 1"
        val m0 = tp.getTokenFor(line, 0)
        assertNull(m0)

        val m1 = tp.getTokenFor(line, 2)
        requireNotNull(m1)
        assertEquals("let", m1.first)
        assertEquals(TokenType.LET, m1.second)

        val eqPos = line.indexOf('=')
        val m2 = tp.getTokenFor(line, eqPos)
        requireNotNull(m2)
        assertEquals("=", m2.first)
        assertEquals(TokenType.EQUAL, m2.second)
    }

    @Test
    fun plus_mergesPatterns_lastWins() {
        val base = TokenProvider.builder(mapOf("\\blet\\b" to TokenType.LET))
        val extra = TokenProvider.builder(mapOf("\\blet\\b" to TokenType.PRINTLN)) // override
        val merged = base + extra

        val line = "let"
        val m = merged.getTokenFor(line, 0)
        requireNotNull(m)
        assertEquals("let", m.first)
        assertEquals(TokenType.PRINTLN, m.second)
    }
}