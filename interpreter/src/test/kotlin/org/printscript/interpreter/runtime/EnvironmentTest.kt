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
        env.assign("a", Value.Num(2.0)); env.assign("a", Value.Num(3.0))
        assertEquals(setOf("a"), env.snapshot().keys)
        assertEquals("3", env.snapshot()["a"])
    }
    @Test fun `usar antes de inicializar falla`() {
        val env = Environment()
        env.declare("x", RType.STRING, null)
        val ex = assertThrows(RuntimeError::class.java) { env.read("x") }
        assertTrue(ex.message!!.contains("antes de inicializar"))
    }
}
