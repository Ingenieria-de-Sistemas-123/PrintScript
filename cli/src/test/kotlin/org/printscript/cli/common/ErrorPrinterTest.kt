package org.printscript.cli.common

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class ErrorPrinterTest {
    @Test
    fun printsSingleErrorToStderr() {
        val errBuffer = ByteArrayOutputStream()
        val oldErr = System.err
        try {
            System.setErr(PrintStream(errBuffer, true, Charsets.UTF_8))
            val pos = Position(1, 2)
            val e =
                LanguageError(
                    message = "Sample message",
                    sourcePath = "file.ps",
                    span = Span(start = pos, end = pos),
                )
            ErrorPrinter.print(e)
        } finally {
            System.setErr(oldErr)
        }
        val text = errBuffer.toString(Charsets.UTF_8)
        assertTrue(text.contains("file.ps:1:2"), "Should include file and position: $text")
        assertTrue(text.contains("Sample message"), "Should include message: $text")
    }

    @Test
    fun printsMultipleErrorsToStderr() {
        val errBuffer = ByteArrayOutputStream()
        val oldErr = System.err
        try {
            System.setErr(PrintStream(errBuffer, true, Charsets.UTF_8))
            val pos = Position(1, 1)
            val e1 = LanguageError("One", "a.ps", Span(pos, pos))
            val e2 = LanguageError("Two", "b.ps", Span(pos, pos))
            ErrorPrinter.printAll(listOf(e1, e2))
        } finally {
            System.setErr(oldErr)
        }
        val text = errBuffer.toString(Charsets.UTF_8)
        assertTrue(text.contains("a.ps"), text)
        assertTrue(text.contains("b.ps"), text)
        assertTrue(text.contains("One"), text)
        assertTrue(text.contains("Two"), text)
    }
}
