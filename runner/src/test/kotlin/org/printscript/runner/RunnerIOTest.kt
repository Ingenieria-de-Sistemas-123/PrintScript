package org.printscript.runner

import org.printscript.interpreter.Interpreter
import org.printscript.interpreter.io.EnvProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.InputProvider
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import kotlin.test.Test
import kotlin.test.assertEquals

class RunnerIOTest {
    // InputProvider que devuelve los valores de una lista en orden
    class ListInputProvider(private val inputs: List<String>) : InputProvider {
        private var index = 0

        override fun readLine(prompt: String): String? = if (index < inputs.size) inputs[index++] else null
    }

    // EnvProvider que devuelve valores de un mapa
    class MapEnvProvider(private val env: Map<String, String>) : EnvProvider {
        override fun get(name: String): String? = env[name]
    }

    @Test
    fun `run program with systemDefault IOContext reads env`() {
        val output = mutableListOf<String>()
        val interpreter = Interpreter(output = { output += it }, ioContext = IOContext.systemDefault())
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_1,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/read-env.ps"))
        stream.use { runner.run(it) }

        assertEquals(listOf(System.getenv("USER")), output)
    }

    @Test
    fun `run program with file input and output`() {
        val output = mutableListOf<String>()
        val ioContext = IOContext(ListInputProvider(listOf("Ada")), MapEnvProvider(emptyMap()))
        val interpreter = Interpreter(output = { output += it }, ioContext = ioContext)
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_1,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/read-input.ps"))
        stream.use { runner.run(it) }

        assertEquals(listOf("Ada"), output)
    }
}
