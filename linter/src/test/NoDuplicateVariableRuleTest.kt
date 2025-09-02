import org.junit.jupiter.api.Assertions.assertEquals
import org.printscript.Linter
import org.printscript.lexer.Lexer
import org.printscript.rules.NoDuplicateVariableRule
import kotlin.test.Test

class NoDuplicateVariableRuleTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test fun `segunda declaracion error`() {
        val ast = parser.parse(lexer.lex("""
      let x: number = 1;
      let x: number = 2;
    """.trimIndent()))
        val issues = Linter(listOf(NoDuplicateVariableRule())).analyze(ast)
        assertEquals(1, issues.size)
        assertEquals("no-duplicate-var", issues[0].ruleId)
    }
}

