package org.printscript.runner

import org.printscript.interpreter.Interpreter
import org.printscript.interpreter.io.EnvProvider
import org.printscript.interpreter.io.FileInputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.parser.DefaultParser
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class RunnerFileIOTest {
    @Test
    fun `run program with file input and output files`() {
        // Archivo de input simulado
        val inputFile = Files.createTempFile("input", ".txt").toFile()
        inputFile.writeText("Ada\n")

        // Archivo de output simulado
        val outputFile = Files.createTempFile("output", ".txt").toFile()

        val inputProvider = FileInputProvider(inputFile.toPath())
        val envProvider =
            EnvProvider { name ->
                when (name) {
                    "INPUT_FILE" -> inputFile.absolutePath
                    "OUTPUT_FILE" -> outputFile.absolutePath
                    else -> null
                }
            }
        val outputProvider = OutputProvider { text -> outputFile.appendText("$text\n") }
        val ioContext = IOContext(inputProvider, envProvider)
        val interpreter = Interpreter(output = outputProvider, ioContext = ioContext)
        val runner =
            Runner(
                tokenProvider = PreConfiguredTokens.TOKENS_1_1,
                parser = DefaultParser(),
                interpreter = interpreter,
            )

        val stream = requireNotNull(javaClass.getResourceAsStream("/programs/v1_1/read-input.ps"))
        stream.use { runner.run(it) }

        // Verifica el output
        val outputLines = outputFile.readLines()
        assertEquals(listOf("Nombre:", "Ada"), outputLines)

        // Limpieza
        inputFile.delete()
        outputFile.delete()
    }
}
