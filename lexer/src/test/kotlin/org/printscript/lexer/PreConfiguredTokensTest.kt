package org.printscript.lexer

import org.junit.jupiter.api.Assertions.assertEquals
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.token.TokenType
import kotlin.test.Test

class PreConfiguredTokensTest {

    @Test
    fun tokens10_basic() {
        val tp = PreConfiguredTokens.TOKENS_1_0
        val line = "let println number string + - * / : ( ) ; = x 42 \"hi\""
        val expectedTypes = listOf(
            TokenType.LET, TokenType.PRINTLN, TokenType.NUMBER_TYPE, TokenType.STRING_TYPE,
            TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.SLASH,
            TokenType.SYNTAX, TokenType.SYNTAX, TokenType.SYNTAX, TokenType.SYNTAX, TokenType.EQUAL,
            TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.STRING
        )

        var pos = 0
        val actual = mutableListOf<TokenType>()
        val lexemes = listOf(
            "let","println","number","string","+","-","*","/",
            ":", "(", ")", ";", "=", "x", "42", "\"hi\""
        )

        for (lex in lexemes) {
            val p = line.indexOf(lex, pos)
            require(p >= 0) { "No se encontró '$lex' a partir de pos=$pos" }
            val (value, type) = tp.getTokenFor(line, p)!!
            actual += type
            pos = p + value.length
        }
        assertEquals(expectedTypes, actual)
    }

    @Test
    fun tokens11_newKeywords() {
        val tp = PreConfiguredTokens.TOKENS_1_1
        val line = "const if else readInput readEnv boolean true false"
        val expected = listOf(
            TokenType.CONST, TokenType.IF, TokenType.ELSE, TokenType.READ_INPUT,
            TokenType.READ_ENV, TokenType.BOOLEAN_TYPE, TokenType.TRUE, TokenType.FALSE
        )
        var pos = 0
        val got = mutableListOf<TokenType>()
        for (lex in line.split(" ")) {
            val p = line.indexOf(lex, pos)
            val m = tp.getTokenFor(line, p)!!
            got += m.second
            pos = p + lex.length
        }
        assertEquals(expected, got)
    }
}