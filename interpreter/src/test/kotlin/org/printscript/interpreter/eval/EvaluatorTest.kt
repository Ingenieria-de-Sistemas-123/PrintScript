package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.util.TestIO
import org.printscript.interpreter.util.minus
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
        val ev = Evaluator(env, TestIO.empty)
        val expr = plus(num(1.0), star(num(2.0), num(3.0)))

        val v = expr.accept(ev)
        assertEquals(Value.Num(7.0), v)
    }

    @Test
    fun `string + string concatena - hola + 2025 = hola2025`() {
        val v = plus(str("hola"), str("2025")).accept(Evaluator(Environment(), TestIO.empty))
        assertEquals(Value.Str("hola2025"), v)
    }

    @Test
    fun `string + number concatena correctamente`() {
        val v = plus(str("hola"), num(2025.0)).accept(Evaluator(Environment(), TestIO.empty))
        assertEquals(Value.Str("hola2025"), v)
    }

    @Test
    fun `number + string concatena correctamente`() {
        val v = plus(num(2025.0), str("hola")).accept(Evaluator(Environment(), TestIO.empty))
        assertEquals(Value.Str("2025hola"), v)
    }

    @Test
    fun `resta con tipos no numericos falla`() {
        val env = Environment()
        val ev = Evaluator(env, TestIO.empty)
        val expr = minus(str("1"), str("2"))
        assertThrows(RuntimeError::class.java) { expr.accept(ev) }
    }

    @Test
    fun `division por cero - lanza RuntimeError`() {
        val env = Environment()
        val ev = Evaluator(env, TestIO.empty)
        val expr = slash(num(1.0), num(0.0))

        assertThrows(RuntimeError::class.java) { expr.accept(ev) }
    }

    @Test
    fun `division normal - 8 div 2 = 4`() {
        val env = Environment()
        val ev = Evaluator(env, TestIO.empty)
        val expr = slash(num(8.0), num(2.0))
        val v = expr.accept(ev)
        assertEquals(Value.Num(4.0), v)
    }
}
