package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.nio.file.Path as NioPath

class FormatCmdTest {
    @TempDir
    lateinit var temp: File

    @TempDir
    lateinit var tmp: NioPath

    @Test
    fun formatCheckShowsDiffWhenNotFormatted() {
        val src = "let a:string=\"x\";println(a);"
        val f = temp.resolve("fmt.ps").apply { writeText(src) }

        val r = CommandRunner.run("format", "-f", f.path, "--check")
        assertTrue(r.exitCode != 0, "Should fail when file is not formatted")
        assertTrue(
            r.stdout.isNotBlank(),
            "stdout should show suggestion or diff. stdout=${r.stdout}",
        )
    }

    @Test
    fun formatApplyRewritesFile() {
        val src = "let a:string=\"x\";println(a);"
        val f = temp.resolve("fmt_apply.ps").apply { writeText(src) }

        val r = CommandRunner.run("format", "-f", f.path, "--apply")
        assertEquals(0, r.exitCode, r.stderr)

        val after = f.readText()
        assertTrue(after != src, "File should have been modified by formatter")
    }

    @Test
    fun formatPrintsToStdout() {
        val src = "let a:string=\"x\";println(a);"
        val f = temp.resolve("fmt_stdout.ps").apply { writeText(src) }

        val r = CommandRunner.run("format", "-f", f.path)
        assertEquals(0, r.exitCode, r.stderr)
        assertTrue(r.stdout.isNotBlank(), "Formatter should print formatted output to stdout")
    }

    @Test
    fun formatFailsOnConflictingFlags() {
        val r = CommandRunner.run("format", "-f", "nonexistent.ps", "--check", "--apply")
        assertEquals(2, r.exitCode, "Should return 2 when --check and --apply are both present")
        assertTrue(
            r.stderr.contains("Use only one") ||
                r.stdout.contains("Use only one"),
            "Should print a helpful message. out=${r.stdout} err=${r.stderr}",
        )
    }

    @Test
    fun checkPassesWhenAlreadyFormatted() {
        val unformatted = "let a:string=\"x\";println(a);"
        val f = temp.resolve("formatted.ps").apply { writeText(unformatted) }

        val apply = CommandRunner.run("format", "-f", f.path, "--apply")
        assertEquals(0, apply.exitCode, apply.stderr)

        val check = CommandRunner.run("format", "-f", f.path, "--check")
        val all = check.stdout + check.stderr
        assertEquals(0, check.exitCode, "Expected already formatted. Output: $all")
        assertTrue(
            all.contains("already formatted", ignoreCase = true) ||
                all.contains("OK:", ignoreCase = true),
            "Should say the file is already formatted. Output: $all",
        )
    }

    @Test
    fun formatFailsWhenParseError() {
        val bad = temp.resolve("bad_fmt.ps").apply { writeText("foo") }
        val r = CommandRunner.run("format", "-f", bad.path, "--check")
        assertTrue(r.exitCode != 0, "Should fail when parse error")
        val all = r.stdout + r.stderr
        assertTrue(all.contains("error:", ignoreCase = true), "Should show error. out=$all")
    }

    @Test
    fun `format --check returns 1 and prints suggested output when needs formatting`() {
        val unformatted = """let a:string="x";println(a);"""
        val f = tmp.resolve("bad.ps").apply { writeText(unformatted) }

        val r = CommandRunner.run("format", "-f", f.toString(), "--check")
        val out = r.stdout + r.stderr
        assertEquals(1, r.exitCode, out)
        assertTrue(out.contains("Suggested output", ignoreCase = true), out)
        assertTrue(out.contains("let a: string = \"x\";"), out)
    }

    @Test
    fun `format --apply writes file and returns 0`() {
        val unformatted = """let a:string="x";println(a);"""
        val f = tmp.resolve("apply.ps").apply { writeText(unformatted) }

        val r = CommandRunner.run("format", "-f", f.toString(), "--apply")
        val out = r.stdout + r.stderr
        assertEquals(0, r.exitCode, out)
        assertTrue(out.contains("Formatting applied", ignoreCase = true), out)

        val check = CommandRunner.run("format", "-f", f.toString(), "--check")
        assertEquals(0, check.exitCode, check.stdout + check.stderr)
    }

    @Test
    fun `format fails with 2 when passing both --check and --apply`() {
        val f = tmp.resolve("conflict.ps").apply { writeText("""let a:string="x";println(a);""") }
        val r = CommandRunner.run("format", "-f", f.toString(), "--check", "--apply")
        assertEquals(2, r.exitCode, r.stdout + r.stderr)
    }

    @Test
    fun formatApplyOnAlreadyFormattedPrintsOK() {
        val src = "let a: string = \"x\";\nprintln(\"x\");\n" // 👈 incluye \n final
        val f = temp.resolve("apply_ok.ps").apply { writeText(src) }

        val r = CommandRunner.run("format", "-f", f.path, "--apply")
        val out = r.stdout + r.stderr
        assertEquals(0, r.exitCode, out)
        assertTrue(
            out.contains("already formatted", ignoreCase = true) ||
                out.contains("OK:", ignoreCase = true),
            "Should say already formatted. Output: $out",
        )
        assertEquals(src, f.readText())
    }
}
