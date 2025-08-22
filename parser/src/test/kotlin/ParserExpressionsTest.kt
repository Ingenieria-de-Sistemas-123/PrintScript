
import node.AssignationNode
import node.DoubleExpressionNode
import node.LiteralNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.printscript.lexer.Lexer
import kotlin.test.Test

private val AssignationNode.value get() = this.type

class ParserExpressionsTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test
    fun `precedencia multiplicativa sobre aditiva`() {
        val code = "x = 1 + 2 * 3;"
        val ast = parser.parse(lexer.lex(code))
        val asg = ast.single() as AssignationNode
        val root = asg.value as DoubleExpressionNode

        assertEquals("+", root.operator)

        val left = root.left as LiteralNode<*>
        val right = root.right as DoubleExpressionNode
        assertEquals("1", left.value)
        assertEquals("*", right.operator)

        val rL = right.left as LiteralNode<*>
        val rR = right.right as LiteralNode<*>
        assertEquals("2", rL.value)
        assertEquals("3", rR.value)
    }

    @Test
    fun `parentesis alteran precedencia`() {
        val code = "x = (1 + 2) * 3;"
        val ast = parser.parse(lexer.lex(code))
        val asg = ast.single() as AssignationNode
        val root = asg.value as DoubleExpressionNode

        assertEquals("*", root.operator)

        val left = root.left as DoubleExpressionNode
        val right = root.right as LiteralNode<*>
        assertEquals("+", left.operator)
        assertEquals("3", right.value)
    }

    @Test
    fun `unario menos como 0 - expr`() {
        val code = "x = -a;"
        val ast = parser.parse(lexer.lex(code))
        val asg = ast.single() as AssignationNode
        val root = asg.value as DoubleExpressionNode
        assertEquals("-", root.operator)

        val l = root.left as LiteralNode<*>
        val r = root.right as LiteralNode<*>
        assertEquals("0", l.value)
        assertEquals("number", l.type)
        assertEquals("a", r.value)
        assertEquals("identifier", r.type)
    }
}