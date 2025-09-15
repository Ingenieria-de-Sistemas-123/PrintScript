package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.ReadEnvIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.util.CapturingOutput
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.TestIO

class ReadEnvDefaultsTest {
    @Test fun `readEnv default string imprime literal`() {
        val env = Environment()
        val out = CapturingOutput()
        val io = TestIO.empty.copy(env = MapEnvProvider(mapOf("GREETING" to "hola")))
        val exec = Executor(env, out, io)
        val prog = listOf(PrintIR(ReadEnvIR("GREETING", expected = null)))
        exec.exec(prog)
        assertEquals(listOf("hola"), out.lines)
    }
}
