package org.printscript.cli.adapters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.parser.ParseException
import java.io.File

class FrontendAdapterSequenceTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun `lazy sequence is not evaluated until iterated`() {
        val source = "let a : string = \"Hi\"; println(a);"
        val file = temp.resolve("prog.ps").apply { writeText(source) }
        val fe = FrontendAdapter("1.0")
        val res = fe.parseProgramSequence(file.readText(), file.path)
        assertTrue(res.isSuccess, "Result should be success even before consuming the sequence")
        // Not iterating here: no exception, proves laziness path executed.
    }

    @Test
    fun `parse error surfaces during sequence iteration`() {
        val source = "foo" // invalido: deberia fallar al intentar generar AST
        val file = temp.resolve("bad.ps").apply { writeText(source) }
        val fe = FrontendAdapter("1.0")
        val res = fe.parseProgramSequence(file.readText(), file.path)
        assertTrue(res.isSuccess, "Creation of sequence succeeds; error deferred to iteration")
        val seq = res.getOrThrow()
        assertThrows(ParseException::class.java) { seq.first() }
    }

    @Test
    fun `lexical error is captured immediately in sequence result`() {
        val source = "\u0001" // caracter inválido para el lexer
        val file = temp.resolve("lex.ps").apply { writeText(source) }
        val fe = FrontendAdapter("1.0")
        val res = fe.parseProgramSequence(file.readText(), file.path)
        assertTrue(res.isFailure, "Lexical error should return failure Result")
        val failure = res.exceptionOrNull() as CliFailure
        assertTrue(failure.error.message.contains("Lex") || failure.error.message.contains("lex"))
    }

    @Test
    fun `unsupported version surfaces as generic failure`() {
        val source = "let a : number = 1;"
        val file = temp.resolve("ver.ps").apply { writeText(source) }
        val fe = FrontendAdapter("2.0") // versión no soportada
        val res = fe.parseProgramSequence(file.readText(), file.path)
        assertTrue(res.isFailure, "Unsupported version should return failure Result")
        val failure = res.exceptionOrNull() as CliFailure
        assertTrue(failure.error.message.contains("Unsupported") || failure.error.message.contains("soportada"))
    }

    @Test
    fun `valid program sequence yields expected number of nodes`() {
        val source =
            """
            let a : number = 1;
            let b : number = 2;
            println(a + b);
            """.trimIndent()
        val file = temp.resolve("ok.ps").apply { writeText(source) }
        val fe = FrontendAdapter("1.0")
        val res = fe.parseProgramSequence(file.readText(), file.path)
        val seq = res.getOrThrow()
        val list = seq.toList()
        assertEquals(3, list.size, "Expected three top-level statements")
    }
}
