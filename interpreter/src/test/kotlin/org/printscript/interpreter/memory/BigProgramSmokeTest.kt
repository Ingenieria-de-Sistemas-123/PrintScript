package org.printscript.interpreter.memory

import org.junit.jupiter.api.Test
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.runtime.Environment

class BigProgramSmokeTest {
    @Test
    fun `10k prints with limited heap`() {
        val exec = Executor(Environment()) { /* no-op output */ }

        repeat(10_000) { i ->
            PrintIR(NumLit(i.toDouble())).accept(exec)
        }
    }
}
