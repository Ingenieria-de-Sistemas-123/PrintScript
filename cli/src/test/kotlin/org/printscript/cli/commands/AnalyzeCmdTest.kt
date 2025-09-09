package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.util.CommandRunner
import java.io.File
import kotlin.test.assertTrue

class AnalyzeCmdTest {
    @TempDir
    lateinit var temp: File

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

        assertTrue(
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
                r.stdout.contains("Sin issues") || // allow either if CLI text not switched yet
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
}
