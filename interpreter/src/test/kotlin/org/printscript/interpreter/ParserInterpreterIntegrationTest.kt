package org.printscript.interpreter

import org.printscript.lexer.Lexer
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import java.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class ParserInterpreterIntegrationTest {
    @Test
    fun `evaluates arithmetic assignment`() {
        val source =
            """
            let total: number = 2 + 3;
            println(total);
            """.trimIndent()

        val (output, interpreter) = runScript(source)

        assertEquals(listOf("5"), output)
        assertEquals("5", interpreter.environmentSnapshot()["total"])
    }

    @Test
    fun `prints string literal without quotes`() {
        val source =
            """
            println("hello");
            """.trimIndent()

        val (output, _) = runScript(source)

        assertEquals(listOf("hello"), output)
    }

    @Test
    fun `if condition reads boolean variable`() {
        val source =
            """
            let shouldGreet: boolean = true;
            if (shouldGreet) {
                println("Hola!");
            } else {
                println("Chau");
            }
            """.trimIndent()

        val (output, interpreter) = runScript(source)

        assertEquals(listOf("Hola!"), output)
        assertEquals("true", interpreter.environmentSnapshot()["shouldGreet"])
    }

    @Test
    fun `if condition false ejecuta else branch`() {
        val source =
            """
        let shouldGreet: boolean = false;
        if (shouldGreet) {
            println("Hola!");
        } else {
            println("Chau");
        }
        """.trimIndent()

        val (output, interpreter) = runScript(source)

        assertEquals(listOf("Chau"), output)
        assertEquals("false", interpreter.environmentSnapshot()["shouldGreet"])
    }

    private fun runScript(source: String): Pair<List<String>, Interpreter> {
        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        val tokens = lexer.lex(StringReader(source))
        val ast = DefaultParser().parse(tokens)
        val buffer = mutableListOf<String>()
        val interpreter = Interpreter(output = { buffer += it })
        interpreter.execute(ast)
        return buffer.toList() to interpreter
    }
}
