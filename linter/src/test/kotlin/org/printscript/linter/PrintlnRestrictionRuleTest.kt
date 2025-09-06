package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.printscript.lexer.Lexer
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.parser.DefaultParser
import org.printscript.parser.Parser
import kotlin.test.Test

class PrintlnRestrictionRuleTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test fun `println con expresion compleja advierte`() {
        val ast = parser.parse(lexer.lex("""println("hi " + 1);"""))
        val issues = Linter(listOf(PrintlnRestrictionRule())).analyze(ast)
        assertEquals(1, issues.size)
        assertEquals("println-restriction", issues[0].ruleId)
    }

    @Test fun `println con identificador OK`() {
        val ast =
            parser.parse(
                lexer.lex(
                    """
                    let name: string = "a";
                    println(name);
                    """.trimIndent(),
                ),
            )
        val issues = Linter(listOf(PrintlnRestrictionRule())).analyze(ast)
        assertTrue(issues.isEmpty())
    }
}
