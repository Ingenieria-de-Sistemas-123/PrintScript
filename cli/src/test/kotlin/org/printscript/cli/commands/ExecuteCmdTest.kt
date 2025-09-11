package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
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

    @Test
    fun help_prints_usage_and_exits_zero() {
        val cmd = CommandLine(ExecuteCmd())
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        cmd.setOut(PrintWriter(out, true))
        cmd.setErr(PrintWriter(err, true))

        val code = cmd.execute("--help")

        assertEquals(0, code)
        val text = out.toString()
        assertTrue(text.contains("Usage:"))
        assertTrue(text.contains("--ps-version") || text.contains("--version"))
        assertTrue(text.contains("--inputs-file"))
        assertEquals("", err.toString())
    }

    @Test
    fun runs_v10_program_exit_zero() {
        val src = Files.createTempFile("ps-v10-", ".ps")
        Files.writeString(
            src,
            """
            let x: number = 1 + 2;
            println(x);
            """.trimIndent(),
        )

        val cmd = CommandLine(ExecuteCmd())
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        cmd.setOut(PrintWriter(out, true))
        cmd.setErr(PrintWriter(err, true))

        val code = cmd.execute("-f", src.toString(), "--ps-version", "1.0")

        assertEquals(0, code)
        assertEquals("", err.toString())
    }

    @Test
    fun runs_with_version_alias() {
        val src = Files.createTempFile("ps-v10-alias-", ".ps")
        Files.writeString(
            src,
            """
            let x: number = 40 + 2;
            println(x);
            """.trimIndent(),
        )

        val cmd = CommandLine(ExecuteCmd())
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        cmd.setOut(PrintWriter(out, true))
        cmd.setErr(PrintWriter(err, true))

        val code = cmd.execute("-f", src.toString(), "--version", "1.0")

        assertEquals(0, code)
        assertEquals("", err.toString())
    }

    @Test
    fun accepts_inputs_file_even_if_not_used() {
        val src = Files.createTempFile("ps-v10-inputs-", ".ps")
        Files.writeString(
            src,
            """
            let x: number = 5 + 5;
            println(x);
            """.trimIndent(),
        )
        val inputs =
            Files.createTempFile("inputs-", ".txt").also {
                Files.writeString(it, "ignored\n")
            }

        val cmd = CommandLine(ExecuteCmd())
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        cmd.setOut(PrintWriter(out, true))
        cmd.setErr(PrintWriter(err, true))

        val code = cmd.execute("-f", src.toString(), "--ps-version", "1.0", "--inputs-file", inputs.toString())

        assertEquals(0, code)
        assertEquals("", err.toString())
    }
}
