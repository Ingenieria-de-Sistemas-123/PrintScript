package org.printscript.interpreter

import org.printscript.lexer.Lexer
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import java.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals

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
            let msg: string = "hola";
            println(msg);
            """.trimIndent()

        val (output, _) = runScript(source)
        assertEquals(listOf("hola"), output)
    }

    @Test
    fun `if condition reads boolean variable`() {
        val source =
            """
            let flag: boolean = true;
            if(flag){
              println("ok");
            } else {
              println("fail");
            }
            """.trimIndent()

        val (output, _) = runScript(source)
        assertEquals(listOf("ok"), output)
    }

    @Test
    fun `if condition false ejecuta else branch`() {
        val source =
            """
            let flag: boolean = false;
            if(flag){
              println("ok");
            } else {
              println("else");
            }
            """.trimIndent()

        val (output, _) = runScript(source)
        assertEquals(listOf("else"), output)
    }

    private fun runScript(source: String): Pair<List<String>, Interpreter> {
        val lexer = Lexer(PreConfiguredTokens.TOKENS_1_1)
        val tokens = lexer.lex(StringReader(source))
        val ast = DefaultParser().parse(tokens)
        val buffer = mutableListOf<String>()
        // Normaliza quitando el \n final provisto al OutputProvider
        val interpreter = Interpreter(output = { printed -> buffer += printed.removeSuffix("\n") })
        interpreter.execute(ast)
        return buffer.toList() to interpreter
    }
}
