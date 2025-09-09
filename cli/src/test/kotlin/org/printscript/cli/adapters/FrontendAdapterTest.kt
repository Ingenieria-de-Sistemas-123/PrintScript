package org.printscript.cli.adapters

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FrontendAdapterTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun parsesValidProgram() {
        val source =
            """
            let name : string = "Toto";
            println(name);
            """.trimIndent()
        val file = temp.resolve("ok.ps").apply { writeText(source) }

        val fe = FrontendAdapter("1.0")
        val res = fe.parseProgram(file.readText(), file.path)

        assertTrue(res.isSuccess, "Should parse successfully")
        assertTrue(res.getOrThrow().isNotEmpty(), "AST should not be empty")
    }

    @Test
    fun lexicalErrorIsMappedToFailure() {
        val source = "\u0001"
        val file = temp.resolve("lex.ps").apply { writeText(source) }

        val fe = FrontendAdapter()
        val res = fe.parseProgram(file.readText(), file.path)

        assertTrue(res.isFailure, "Should fail with lexical error")
        val failure = res.exceptionOrNull() as CliFailure
        val msg = failure.error.message.lowercase()
        assertTrue(
            msg.contains("lex") ||
                msg.contains("caracter inesperado") ||
                msg.contains("unexpected"),
            "Message was: ${failure.error.message}",
        )
    }

    @Test
    fun parseErrorIsMappedToFailure() {
        val source = "foo"
        val file = temp.resolve("parse.ps").apply { writeText(source) }

        val fe = FrontendAdapter()
        val res = fe.parseProgram(file.readText(), file.path)

        assertTrue(res.isFailure, "Should fail with parse error")
        val failure = res.exceptionOrNull() as CliFailure
        val msg = failure.error.message.lowercase()
        assertTrue(
            msg.contains("assignment") ||
                msg.contains("parse") ||
                msg.contains("asignación") ||
                msg.contains("se esperaba"),
            "Message was: ${failure.error.message}",
        )
        assertEquals(failure.error.span.start, failure.error.span.end)
    }

    @Test
    fun loadSourceReadsFileContent() {
        val content =
            """
            let name : string = "Toto";
            println(name);
            """.trimIndent()
        val f = temp.resolve("sample.ps").apply { writeText(content) }

        val fe = FrontendAdapter()
        val read = fe.loadSource(f.path)
        assertEquals(content, read)
    }
}
