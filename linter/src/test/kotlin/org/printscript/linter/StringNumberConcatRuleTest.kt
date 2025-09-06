package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.printscript.lexer.Lexer
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.parser.DefaultParser
import org.printscript.parser.Parser
import kotlin.test.Ignore
import kotlin.test.Test

class StringNumberConcatRuleTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Ignore
    @Test
    fun `string + number advierte`() {
        val ast = parser.parse(lexer.lex("""let s: string = "x"; println(s + 1);"""))
        val issues = Linter(listOf(StringNumberConcatRule())).analyze(ast)
        // deberia de dar un warning de string-number-concat por lo tanto issues no deberia estar vacio
        assertTrue(issues.isNotEmpty())
        assertEquals(1, issues.size)
        assertEquals("string-number-concat", issues[0].ruleId)
    }

    @Test fun `number + number OK`() {
        val ast = parser.parse(lexer.lex("""println(1 + 2);"""))
        val issues = Linter(listOf(StringNumberConcatRule())).analyze(ast)
        assertTrue(issues.isEmpty())
    }
}
