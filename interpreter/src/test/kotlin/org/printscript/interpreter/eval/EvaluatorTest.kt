package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.testutil.*

class EvaluatorTest {
    //expresiones - num, str, id, binary

    @Test
    fun `suma y precedencia`() {
        val env = Environment()
        val ev = Evaluator(env)
        val expr = plus(num(1.0), star(num(2.0), num(3.0)))

        val v = expr.accept(ev)
        assertEquals(Value.Num(7.0), v)
    }

    @Test
    fun `concatenacion con string - hola + 2025 = hola2025`() {
        val env = Environment()
        val ev = Evaluator(env)
        val expr = plus(str("hola"), num(2025.0))

        val v = expr.accept(ev)
        assertEquals(Value.Str("hola2025"), v)
    }

    @Test
    fun `division por cero - lanza RuntimeError`() {
        val env = Environment()
        val ev = Evaluator(env)
        val expr = slash(num(1.0), num(0.0))

        assertThrows(RuntimeError::class.java) { expr.accept(ev) }
    }
}
