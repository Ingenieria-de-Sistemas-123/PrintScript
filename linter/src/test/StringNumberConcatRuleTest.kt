import org.junit.jupiter.api.Assertions.*
import org.printscript.Linter
import org.printscript.lexer.Lexer
import org.printscript.rules.StringNumberConcatRule
import kotlin.test.Test

class StringNumberConcatRuleTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test fun `string + number advierte`() {
        val ast = parser.parse(lexer.lex("""let s: string = "x"; println(s + 1);"""))
        val issues = Linter(listOf(StringNumberConcatRule())).analyze(ast)
        assertEquals(1, issues.size)
        assertEquals("string-number-concat", issues[0].ruleId)
    }

    @Test fun `number + number OK`() {
        val ast = parser.parse(lexer.lex("""println(1 + 2);"""))
        val issues = Linter(listOf(StringNumberConcatRule())).analyze(ast)
        assertTrue(issues.isEmpty())
    }
}