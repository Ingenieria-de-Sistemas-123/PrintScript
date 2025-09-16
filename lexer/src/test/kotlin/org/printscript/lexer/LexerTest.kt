package org.printscript.lexer

import org.junit.jupiter.api.Test
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.token.TokenType
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LexerTest {
    @Test
    fun lex_flat_includesEOF_and_positions() {
        val code =
            """
            let x: number = 1 + 2;
            println("hi");
            """.trimIndent()

        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        val tokens = lexer.lex(StringReader(code))
        val types = tokens.map { it.type }

        assertTrue(types.contains(TokenType.LET))
        assertTrue(types.contains(TokenType.IDENTIFIER))
        assertTrue(types.contains(TokenType.NUMBER_TYPE))
        assertTrue(types.contains(TokenType.NUMBER))
        assertTrue(types.contains(TokenType.PLUS))
        assertTrue(types.contains(TokenType.PRINTLN))
        assertTrue(types.contains(TokenType.STRING))
        assertEquals(TokenType.EOF, types.last())

        tokens.forEach { t ->
            assertTrue(t.line >= 1)
            assertTrue(t.column >= 1)
        }
    }

    @Test
    fun lexLines_iterates_by_nonblank_lines() {
        val code =
            """

            let a: number = 1;

            println(a);
            """.trimIndent()

        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        val it = lexer.lexLines(StringReader(code))

        val l1 = it.next()
        assertEquals(TokenType.LET, l1.first().type)

        val l2 = it.next()
        assertEquals(TokenType.PRINTLN, l2.first().type)

        assertTrue(!it.hasNext())
    }

    @Test
    fun lex_throws_on_unexpected_char() {
        val code = "let x: number = $ 10;"
        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        assertFailsWith<IllegalStateException> {
            lexer.lex(StringReader(code))
        }
    }
}
