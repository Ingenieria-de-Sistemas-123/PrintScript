package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.ConstDeclIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.util.TestIO

class ConstDeclTypeMismatchTest {
    @Test fun `const con tipo incompatible lanza IllegalArgumentException`() {
        val env = Environment()
        val exec = Executor(env, { }, TestIO.empty)
        val prog = listOf(ConstDeclIR("c", RType.NUMBER, StrLit("oops")))
        val ex = assertThrows(IllegalArgumentException::class.java) { exec.exec(prog) }
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }
}
