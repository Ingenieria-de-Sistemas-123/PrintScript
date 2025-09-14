package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.ReadInputIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.CapturingOutput
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.interpreter.util.TestIO

class ReadInputTest {
    @Test fun `readInput number ok`() {
        val env = Environment()
        val out = CapturingOutput()
        val io = TestIO.empty.copy(input = QueueInputProvider(listOf("42")))
        val exec = Executor(env, out, io)
        val prog =
            listOf(
                DeclIR("n", RType.NUMBER, ReadInputIR("n?", RType.NUMBER)),
                PrintIR(IdRef("n")),
            )
        exec.exec(prog)
        assertEquals(listOf("42"), out.lines)
    }

    @Test fun `readInput number invalido`() {
        val env = Environment()
        val io = TestIO.empty.copy(input = QueueInputProvider(listOf("abc")))
        val exec = Executor(env, org.printscript.interpreter.io.OutputProvider { }, io)
        val prog = listOf(DeclIR("n", RType.NUMBER, ReadInputIR("n?", RType.NUMBER)))
        assertThrows(RuntimeError::class.java) { exec.exec(prog) }
    }

    @Test fun `readInput boolean true imprime true`() {
        val env = Environment()
        val out = CapturingOutput()
        val io = TestIO.empty.copy(input = QueueInputProvider(listOf("true")))
        val exec = Executor(env, out, io)

        val prog =
            listOf(
                DeclIR("b", RType.BOOLEAN, ReadInputIR("flag?", RType.BOOLEAN)),
                PrintIR(IdRef("b")),
            )

        exec.exec(prog)
        assertEquals(listOf("true"), out.lines)
    }
}
