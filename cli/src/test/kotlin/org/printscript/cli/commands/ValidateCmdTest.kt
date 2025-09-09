package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateCmdTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun validateOk() {
        val src =
            """
            let a : string = "x";
            println(a);
            """.trimIndent()
        val f = temp.resolve("ok.ps").apply { writeText(src) }

        val r = CommandRunner.run("validate", "-f", f.path)
        assertEquals(0, r.exitCode, r.stderr)
        assertTrue(r.stdout.contains("OK:"), "stdout was: ${r.stdout}")
    }

    @Test
    fun validateError() {
        val f = temp.resolve("bad.ps").apply { writeText("foo") }
        val r = CommandRunner.run("validate", "-f", f.path)
        assertTrue(r.exitCode != 0, "Should fail on invalid input")
        assertTrue(r.stderr.contains("error:"), "stderr was: ${r.stderr}")
    }
}
