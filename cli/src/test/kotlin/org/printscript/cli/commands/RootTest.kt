package org.printscript.cli.commands

import org.junit.jupiter.api.Test
import org.printscript.cli.util.CommandRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RootTest {
    @Test
    fun showsHelp() {
        val r = CommandRunner.run("--help")
        assertEquals(0, r.exitCode, r.stderr)
        assertTrue(r.stdout.contains("Usage: printscript"), "Should print usage/help")
    }

    @Test
    fun showsVersion() {
        val r = CommandRunner.run("--version")
        assertEquals(0, r.exitCode, r.stderr)
        // Accept either explicit version text or any non-empty line
        assertTrue(r.stdout.isNotBlank(), "Version should be printed")
    }

    @Test
    fun unknownCommandShowsErrorAndUsage() {
        val r = CommandRunner.run("does-not-exist")
        assertTrue(r.exitCode != 0, "Exit code should be non-zero for unknown command")
        val all = r.stdout + r.stderr
        val mentionsUnknown =
            all.contains("Unknown", ignoreCase = true) ||
                all.contains("unmatched", ignoreCase = true) ||
                all.contains("no such", ignoreCase = true) ||
                all.contains("desconocido", ignoreCase = true)

        val showsUsage =
            all.contains("Usage: printscript") || all.contains("Uso: printscript")

        assertTrue(
            mentionsUnknown || showsUsage,
            "Should show an error/usage for unknown command. out=$all",
        )
    }
}
