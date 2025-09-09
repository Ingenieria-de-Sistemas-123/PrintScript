package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteCmdTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun executePrintsOutput() {
        val src =
            """
            let name : string = "Toto";
            println(name);
            """.trimIndent()
        val f = temp.resolve("hello.ps").apply { writeText(src) }

        val r = CommandRunner.run("execute", "-f", f.path)
        assertEquals(0, r.exitCode, r.stderr)
        assertTrue(
            r.stdout.contains("Toto"),
            "Execution should print 'Toto'. stdout=${r.stdout}",
        )
    }

    @Test
    fun executeFailsOnParseError() {
        val f = temp.resolve("bad_exec.ps").apply { writeText("foo") }
        val r = CommandRunner.run("execute", "-f", f.path)
        assertTrue(r.exitCode != 0, "Execute should fail for invalid source")
        assertTrue(
            r.stderr.contains("error:") || r.stdout.contains("error:"),
            "Should print an error. out=${r.stdout} err=${r.stderr}",
        )
    }
}
