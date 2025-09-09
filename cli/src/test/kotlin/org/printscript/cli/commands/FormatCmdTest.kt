package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormatCmdTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun formatCheckShowsDiffWhenNotFormatted() {
        // Intentionally unformatted: no spaces around ':' and '=' and everything on one line
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
}
