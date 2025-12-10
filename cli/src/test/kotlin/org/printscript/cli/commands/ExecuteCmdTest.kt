package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteCmdTest {
    @TempDir
    lateinit var temp: File

    @TempDir
    lateinit var tmp: Path

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
    fun `execute prints to stdout`() {
        val f = tmp.resolve("hello.ps").apply { writeText("""println("hi");""") }
        val r = CommandRunner.run("execute", "-f", f.toString())
        assertEquals(0, r.exitCode, r.stdout + r.stderr)
        assertTrue(r.stdout.contains("hi"), r.stdout)
    }

    @Test
    fun `execute returns non-zero on simple runtime error`() {
        val f = tmp.resolve("rt.ps").apply { writeText("""let a: number; println(a);""") }
        val r = CommandRunner.run("execute", "-f", f.toString())
        assertTrue(r.exitCode != 0, "Runtime error should be non-zero")
    }

    @Test
    fun `execute reads input from file when provided`() {
        val inputFile = temp.resolve("in.txt").apply { writeText("Ada\n") }
        val program =
            Paths.get("..", "runner", "src", "test", "resources", "programs", "v1_1", "read-input.ps")
                .toFile()
        require(program.exists()) { "Program not found at ${program.path}" }

        val r =
            CommandRunner.run(
                "execute",
                "-f",
                program.path,
                "--version",
                "1.1",
                "--input-file",
                inputFile.path,
            )

        assertEquals(0, r.exitCode, r.stderr)
        assertTrue(r.stdout.contains("Nombre:"), "Prompt should be printed. stdout=${r.stdout}")
        assertTrue(r.stdout.contains("Ada"), "Input value should be echoed. stdout=${r.stdout}")
    }

    @Test
    fun `execute fails gracefully when input file does not exist`() {
        val missing = temp.resolve("missing.txt")
        val program =
            Paths.get("..", "runner", "src", "test", "resources", "programs", "v1_1", "read-input.ps")
                .toFile()

        val r =
            CommandRunner.run(
                "execute",
                "-f",
                program.path,
                "--version",
                "1.1",
                "--input-file",
                missing.path,
            )

        assertTrue(r.exitCode != 0, "Should fail when input file is missing")
        assertTrue(
            r.stderr.contains("Could not open input file"),
            "Should report missing input file. stderr=${r.stderr}",
        )
    }
}
