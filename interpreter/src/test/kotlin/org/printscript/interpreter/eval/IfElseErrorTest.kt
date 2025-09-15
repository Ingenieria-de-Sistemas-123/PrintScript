package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.TestIO

class IfElseErrorTest {
    @Test fun `if con condicion no booleana falla`() {
        val env = Environment()
        val exec = Executor(env, { }, TestIO.empty)
        val prog =
            listOf(
                DeclIR("b", RType.NUMBER, NumLit(1.0)),
                IfIR("b", listOf(PrintIR(StrLit("then"))), listOf(PrintIR(StrLit("else")))),
            )
        val ex = assertThrows(RuntimeError::class.java) { exec.exec(prog) }
        assertTrue(ex.message!!.contains("boolean"))
    }

    @Test fun `if con variable inexistente falla`() {
        val env = Environment()
        val exec = Executor(env, { }, TestIO.empty)
        val prog = listOf(IfIR("nope", listOf(PrintIR(StrLit("x"))), null))
        val ex = assertThrows(RuntimeError::class.java) { exec.exec(prog) }
        assertTrue(ex.message!!.contains("no definida"))
    }
}
