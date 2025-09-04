import org.junit.jupiter.api.Assertions.*
import org.printscript.Linter
import org.printscript.lexer.Lexer
import org.printscript.rules.PrintlnRestrictionRule
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
        val ast = parser.parse(lexer.lex("""
      let name: string = "a";
      println(name);
    """.trimIndent()))
        val issues = Linter(listOf(PrintlnRestrictionRule())).analyze(ast)
        assertTrue(issues.isEmpty())
    }
}