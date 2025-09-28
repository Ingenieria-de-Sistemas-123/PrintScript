package org.printscript.runner

import org.printscript.interpreter.Interpreter
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RunnerTest {
    @Test
    fun `run 1_0 program populates environment and prints`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner =
            Runner(
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
        val runner =
            Runner(
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

    @Test
    fun `run 1_0 program handles arithmetic operations`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_0,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_0/operations.ps"))
        stream.use { runner.run(it) }

        assertEquals(
            mapOf(
                "base" to "10",
                "fraction" to "3.5",
                "result" to "7",
            ),
            interpreter.environmentSnapshot(),
        )
        assertEquals(listOf("7", "3"), capturedOutput)
    }

    @Test
    fun `run 1_1 program handles boolean declarations`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_1,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/boolean-and-const.ps"))
        stream.use { runner.run(it) }

        assertEquals(
            mapOf(
                "enabled" to "true",
                "status" to "done",
            ),
            interpreter.environmentSnapshot(),
        )
        assertEquals(listOf("true", "done"), capturedOutput)
    }

    @Test
    fun `run 1_1 program fails when const is reassigned`() {
        val capturedOutput = mutableListOf<String>()
        val interpreter = Interpreter { capturedOutput += it }
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_1,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val error =
            assertFailsWith<RuntimeError> {
                requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/const-reassign.ps")).use {
                    runner.run(it)
                }
            }

        assertEquals("No se puede reasignar la constante 'pi'", error.message)
        assertEquals(mapOf("pi" to "3"), interpreter.environmentSnapshot())
        assertEquals(emptyList(), capturedOutput)
    }
}
