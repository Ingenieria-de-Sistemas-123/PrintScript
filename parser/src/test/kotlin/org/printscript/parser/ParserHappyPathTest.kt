package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.printscript.lexer.Lexer
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode
import kotlin.test.Test

private val AssignationNode.value get() = this.type

class ParserHappyPathTest {
    private val lexer = Lexer()
    private val parser: Parser = DefaultParser()

    @Test
    fun `declaracion y println`() {
        val code =
            """
            let name: string = "Joe";
            println(name);
            """.trimIndent()

        val tokens = lexer.lex(code)
        val ast = parser.parse(tokens)

        assertEquals(2, ast.size)

        val decl = ast[0] as DeclarationNode
        assertEquals("name", decl.name)
        assertEquals("string", decl.type)
        val declVal = decl.value as LiteralNode<*>
        assertEquals("Joe", declVal.value)
        assertEquals("string", declVal.type)

        val print = ast[1] as PrintNode
        val arg = print.expression as LiteralNode<*>
        assertEquals("name", arg.value)
        assertEquals("identifier", arg.type)
    }

    @Test
    fun `asignacion simple`() {
        val code = "x = 5;"
        val ast = parser.parse(lexer.lex(code))
        assertEquals(1, ast.size)

        val asg = ast[0] as AssignationNode
        assertEquals("x", asg.name)
        val lit = asg.value as LiteralNode<*>
        assertEquals("5", lit.value)
        assertEquals("number", lit.type)
    }
}
