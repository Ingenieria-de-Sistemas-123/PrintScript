package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.util.num
import org.printscript.interpreter.util.plus
import org.printscript.interpreter.util.slash
import org.printscript.interpreter.util.star
import org.printscript.interpreter.util.str

class EvaluatorTest {
    // expresiones - num, str, id, binary

    @Test
    fun `suma y precedencia`() {
        val env = Environment()
        val ev = Evaluator(env)
        val expr = plus(num(1.0), star(num(2.0), num(3.0)))

        val v = expr.accept(ev)
        assertEquals(Value.Num(7.0), v)
    }

    @Test
    fun `string + string concatena - hola + 2025 = hola2025`() {
        val v = plus(str("hola"), str("2025")).accept(Evaluator(Environment()))
        assertEquals(Value.Str("hola2025"), v)
    }

    @Test
    fun `string + number lanza error en '+' estricto`() {
        assertThrows(RuntimeError::class.java) {
            plus(str("hola"), num(2025.0)).accept(Evaluator(Environment()))
        }
    }

    @Test
    fun `division por cero - lanza RuntimeError`() {
        val env = Environment()
        val ev = Evaluator(env)
        val expr = slash(num(1.0), num(0.0))

        assertThrows(RuntimeError::class.java) { expr.accept(ev) }
    }
}
