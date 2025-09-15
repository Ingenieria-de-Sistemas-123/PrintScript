package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.ReadInputIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.CapturingOutput
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.interpreter.util.TestIO

class ReadInputDefaultsTest {
    @Test fun `readInput default string imprime literal`() {
        val env = Environment()
        val out = CapturingOutput()
        val io = TestIO.empty.copy(input = QueueInputProvider(listOf("hola")))
        val exec = Executor(env, out, io)
        val prog = listOf(PrintIR(ReadInputIR("?", expected = null)))
        exec.exec(prog)
        assertEquals(listOf("hola"), out.lines)
    }

    @Test fun `readInput sin entrada lanza error`() {
        val env = Environment()
        val exec = Executor(env, { }, TestIO.empty) // input devuelve null
        val prog = listOf(PrintIR(ReadInputIR("?", expected = null)))
        assertThrows(RuntimeError::class.java) { exec.exec(prog) }
    }
}
