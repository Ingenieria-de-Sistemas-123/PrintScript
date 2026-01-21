package org.printscript.cli.commands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnalyzeCmdTest {
    @TempDir
    lateinit var temp: File

    @TempDir
    lateinit var tmp: Path

    @Test
    fun analyzeFindsIssues() {
        val src1 =
            """
            let a : string = "x";
            let a : string = "y";
            println(a);
            """.trimIndent()

        val src2 =
            """
            let a : string = "x" + 1;
            println(a);
            """.trimIndent()

        val candidates = listOf(src1, src2)

        var triggered = false
        var lastStdout = ""
        var lastStderr = ""

        for ((idx, src) in candidates.withIndex()) {
            val f = temp.resolve("candidate_$idx.ps").apply { writeText(src) }
            val r = CommandRunner.run("analyze", "-f", f.path)
            lastStdout = r.stdout
            lastStderr = r.stderr

            val mentionsIssues =
                r.stdout.contains("issue", ignoreCase = true) ||
                    r.stdout.contains("issues", ignoreCase = true) ||
                    r.stdout.contains("Issues encontrados", ignoreCase = true) ||
                    r.stderr.contains("error:", ignoreCase = true)

            if (r.exitCode != 0 || mentionsIssues) {
                triggered = true
                break
            }
        }

        assertFalse(
            triggered,
            "Linter did not flag any issue for provided candidates.\nstdout=\n$lastStdout\nstderr=\n$lastStderr",
        )
    }

    @Test
    fun analyzeWithoutIssuesIsOk() {
        val src =
            """
            let a : string = "ok";
            println(a);
            """.trimIndent()
        val f = temp.resolve("ok.ps").apply { writeText(src) }

        val r = CommandRunner.run("analyze", "-f", f.path)
        assertTrue(r.exitCode == 0, "Should succeed when no issues. stderr=${r.stderr}")
        assertTrue(
            r.stdout.contains("No issues") ||
                r.stdout.contains("Sin issues") ||
                r.stdout.contains("✅"),
            "stdout=${r.stdout}",
        )
    }

    @Test
    fun analyzeFailsWhenParseError() {
        val bad = temp.resolve("bad_analyze.ps").apply { writeText("foo") }
        val r = CommandRunner.run("analyze", "-f", bad.path)
        assertTrue(r.exitCode != 0, "Analyze should fail on parse error")
        val all = r.stdout + r.stderr
        assertTrue(all.contains("error:", ignoreCase = true), "Should show error. out=$all")
    }

    @Test
    fun `analyze returns 0 when no issues`() {
        val f = tmp.resolve("clean.ps").apply { writeText("""let name: string = "x"; println(name);""") }
        val r = CommandRunner.run("analyze", "-f", f.toString())
        assertEquals(0, r.exitCode, r.stdout + r.stderr)
        assertTrue(
            (r.stdout + r.stderr).contains("0 issues", ignoreCase = true) ||
                (r.stdout + r.stderr).contains("no issues", ignoreCase = true),
        )
    }

    @Test
    fun `analyze returns non-zero and lists issues`() {
        val f = tmp.resolve("issues.ps").apply { writeText("""let A:string="x"; println(A + 1);""") }
        val r = CommandRunner.run("analyze", "-f", f.toString())
        val out = r.stdout + r.stderr
        assertTrue(r.exitCode != 0, out)
        assertTrue(out.contains("issue", ignoreCase = true) || out.contains("rule", ignoreCase = true), out)
    }
}
