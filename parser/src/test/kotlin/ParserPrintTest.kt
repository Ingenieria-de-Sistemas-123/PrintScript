import org.printscript.lexer.Lexer
import node.DoubleExpressionNode
import node.LiteralNode
import node.PrintNode
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ParserPrintTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test
    fun `println con expresion`() {
        val code = """println("hi " + name);"""
        val ast = parser.parse(lexer.lex(code))
        val pr = ast.single() as PrintNode
        val add = pr.expression as DoubleExpressionNode

        val left = add.left as LiteralNode<*>
        val right = add.right as LiteralNode<*>
        assertEquals("+", add.operator)
        assertEquals("hi ", left.value)
        assertEquals("string", left.type)
        assertEquals("name", right.value)
        assertEquals("identifier", right.type)
    }
}