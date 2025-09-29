package org.printscript.interpreter.memory

import org.junit.jupiter.api.Test
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.util.TestIO

class BigProgramSmokeTest {
    @Test
    fun `10k prints with limited heap`() {
        val exec = Executor(Environment(), { }, TestIO.empty)

        repeat(10_000) { i ->
            PrintIR(NumLit(i.toDouble())).accept(exec)
        }
    }
}
