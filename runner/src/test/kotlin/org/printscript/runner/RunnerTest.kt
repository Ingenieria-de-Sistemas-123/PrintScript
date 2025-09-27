package org.printscript.runner

import kotlin.test.Test
import kotlin.test.assertEquals
import org.printscript.interpreter.Interpreter
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser

class RunnerTest {
    @Test
    fun `run 1_0 program populates environment and prints`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner = Runner(
            tokenProvider = PreConfiguredTokens.TOKENS_1_0,
            parser = DefaultParser(),
            interpreter = interpreter,
        )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_0/simple.ps"))
        stream.use { runner.run(it) }

        assertEquals(
            mapOf(
                "total" to "8",
                "message" to "Hola mundo",
            ),
            interpreter.environmentSnapshot(),
        )
        assertEquals(listOf("Hola mundo"), capturedOutput)
    }

    @Test
    fun `run 1_1 program executes const declarations`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner = Runner(
            tokenProvider = PreConfiguredTokens.TOKENS_1_1,
            parser = DefaultParser(),
            interpreter = interpreter,
        )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/const-and-print.ps"))
        stream.use { runner.run(it) }

        assertEquals(
            mapOf(
                "greeting" to "Hola",
                "count" to "2",
            ),
            interpreter.environmentSnapshot(),
        )
        assertEquals(listOf("Hola", "Hola!"), capturedOutput)
    }
}