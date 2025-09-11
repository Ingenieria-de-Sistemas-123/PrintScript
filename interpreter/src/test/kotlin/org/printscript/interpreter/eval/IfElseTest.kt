package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.util.CapturingOutput
import org.printscript.interpreter.util.TestIO

class IfElseTest {
    @Test fun `if true ejecuta then`() {
        val env = Environment()
        val out = CapturingOutput()
        val exec = Executor(env, out, TestIO.empty)
        val prog =
            listOf(
                DeclIR("b", RType.BOOLEAN, BoolLit(true)),
                IfIR("b", listOf(PrintIR(StrLit("T"))), listOf(PrintIR(StrLit("F")))),
            )
        exec.exec(prog)
        assertEquals(listOf("T"), out.lines)
    }

    @Test fun `if false ejecuta else`() {
        val env = Environment()
        val out = CapturingOutput()
        val exec = Executor(env, out, TestIO.empty)
        val prog =
            listOf(
                DeclIR("b", RType.BOOLEAN, BoolLit(false)),
                IfIR("b", listOf(PrintIR(StrLit("T"))), listOf(PrintIR(StrLit("F")))),
            )
        exec.exec(prog)
        assertEquals(listOf("F"), out.lines)
    }

    @Test fun `if false sin else no imprime nada`() {
        val env = Environment()
        val out = CapturingOutput()
        val exec = Executor(env, out, TestIO.empty)

        val prog =
            listOf(
                DeclIR("cond", RType.BOOLEAN, BoolLit(false)),
                IfIR("cond", thenBlock = listOf(PrintIR(StrLit("then"))), elseBlock = null),
            )

        exec.exec(prog)
        assertEquals(emptyList<String>(), out.lines)
    }
}
