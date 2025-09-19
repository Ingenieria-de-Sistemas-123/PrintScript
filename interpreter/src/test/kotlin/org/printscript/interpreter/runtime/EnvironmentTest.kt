package org.printscript.interpreter.runtime

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EnvironmentTest {
    @Test fun `declara y lee numero`() {
        val env = Environment()
        env.declare("a", RType.NUMBER, Value.Num(1.5))
        assertEquals(Value.Num(1.5), env.read("a"))
    }

    @Test fun `asignar no crea nuevas entradas`() {
        val env = Environment()
        env.declare("a", RType.NUMBER, Value.Num(1.0))
        env.assign("a", Value.Num(2.0))
        env.assign("a", Value.Num(3.0))
        assertEquals(setOf("a"), env.snapshot().keys)
        assertEquals("3", env.snapshot()["a"])
    }

    @Test fun `usar antes de inicializar falla`() {
        val env = Environment()
        env.declare("x", RType.STRING, null)
        val ex = assertThrows(RuntimeError::class.java) { env.read("x") }
        assertTrue(ex.message!!.contains("antes de inicializar"))
    }

    @Test fun `declara y lee constante`() {
        val env = Environment()
        env.declareConst("c", RType.NUMBER, Value.Num(42.0))
        assertEquals(Value.Num(42.0), env.read("c"))
    }

    @Test fun `no permite reasignar constante`() {
        val env = Environment()
        env.declareConst("c", RType.NUMBER, Value.Num(1.0))
        val ex = assertThrows(RuntimeError::class.java) { env.assign("c", Value.Num(2.0)) }
        assertTrue(ex.message!!.contains("No se puede reasignar"))
    }

    @Test fun `no permite declarar dos veces la misma variable`() {
        val env = Environment()
        env.declare("x", RType.NUMBER, Value.Num(1.0))
        val ex = assertThrows(RuntimeError::class.java) { env.declare("x", RType.NUMBER, Value.Num(2.0)) }
        assertTrue(ex.message!!.contains("ya definida"))
    }

    @Test fun `no permite declarar dos veces la misma constante`() {
        val env = Environment()
        env.declareConst("c", RType.NUMBER, Value.Num(1.0))
        val ex = assertThrows(RuntimeError::class.java) { env.declareConst("c", RType.NUMBER, Value.Num(2.0)) }
        assertTrue(ex.message!!.contains("ya definida"))
    }

    @Test fun `asignacion incompatible de tipo falla`() {
        val env = Environment()
        env.declare("x", RType.NUMBER, Value.Num(1.0))
        val ex = assertThrows(RuntimeError::class.java) { env.assign("x", Value.Str("hola")) }
        assertTrue(ex.message!!.contains("Asignación incompatible"))
    }
}
