package org.printscript.interpreter

import kotlin.test.Test
import kotlin.test.assertEquals
import org.printscript.interpreter.io.OutputProvider
import org.printscript.lexer.Lexer
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import java.io.StringReader

class ParserInterpreterIntegrationTest {
    @Test
    fun `evaluates arithmetic assignment`() {
        val source = """
            let total: number = 2 + 3;
            println(total);
        """.trimIndent()

        val (output, interpreter) = runScript(source)

        assertEquals(listOf("5"), output)
        assertEquals("5", interpreter.environmentSnapshot()["total"])
    }

    @Test
    fun `prints string literal without quotes`() {
        val source = """
            println("hello");
        """.trimIndent()

        val (output, _) = runScript(source)

        assertEquals(listOf("hello"), output)
    }

    private fun runScript(source: String): Pair<List<String>, Interpreter> {
        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        val tokens = lexer.lex(StringReader(source))
        val ast = DefaultParser().parse(tokens)
        val buffer = mutableListOf<String>()
        val interpreter = Interpreter(output = OutputProvider { buffer += it })
        interpreter.execute(ast)
        return buffer.toList() to interpreter
    }
}