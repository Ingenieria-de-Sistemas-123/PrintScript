package org.printscript.cli.util

import org.printscript.cli.Root
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter

data class CmdResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
)

object CommandRunner {
    @Synchronized
    fun run(vararg args: String): CmdResult {
        val outBuffer = ByteArrayOutputStream()
        val errBuffer = ByteArrayOutputStream()
        val outPs = PrintStream(outBuffer, true, Charsets.UTF_8)
        val errPs = PrintStream(errBuffer, true, Charsets.UTF_8)

        val originalOut = System.out
        val originalErr = System.err

        try {
            System.setOut(outPs)
            System.setErr(errPs)

            val cmd = CommandLine(Root())
            cmd.out = PrintWriter(outPs, true)
            cmd.err = PrintWriter(errPs, true)
            cmd.isCaseInsensitiveEnumValuesAllowed = true

            val code = cmd.execute(*args)

            outPs.flush()
            errPs.flush()
            val stdout = outBuffer.toString(Charsets.UTF_8)
            val stderr = errBuffer.toString(Charsets.UTF_8)
            return CmdResult(code, stdout, stderr)
        } finally {
            System.setOut(originalOut)
            System.setErr(originalErr)
            outPs.close()
            errPs.close()
        }
    }
}
