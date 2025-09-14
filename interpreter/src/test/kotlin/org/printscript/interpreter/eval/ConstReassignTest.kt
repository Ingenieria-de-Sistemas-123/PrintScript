package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.ConstDeclIR
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.TestIO

class ConstReassignTest {
    @Test fun `reasignar const falla`() {
        val env = Environment()
        val exec = Executor(env, { }, TestIO.empty)
        val prog =
            listOf(
                ConstDeclIR("c", RType.NUMBER, NumLit(1.0)),
                AssignIR("c", NumLit(2.0)),
            )
        val ex = assertThrows(RuntimeError::class.java) { exec.exec(prog) }
        assertTrue(ex.message!!.contains("const"))
    }
}
