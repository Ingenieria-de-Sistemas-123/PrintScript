package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.StmtIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.BufferOutput
import org.printscript.interpreter.util.TestIO
import org.printscript.interpreter.util.assign
import org.printscript.interpreter.util.decl
import org.printscript.interpreter.util.id
import org.printscript.interpreter.util.num
import org.printscript.interpreter.util.plus
import org.printscript.interpreter.util.print
import org.printscript.interpreter.util.star
import org.printscript.interpreter.util.str

class ExecutorTest {
    @Test
    fun `declara x y lo imprime`() {
        val env = Environment()
        val out = BufferOutput()
        val exec = Executor(env, out, TestIO.empty)

        val prog =
            listOf<StmtIR>(
                decl("x", RType.NUMBER, plus(num(1.0), star(num(2.0), num(3.0)))),
                print(id("x")),
            )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("7"), out.lines)
        assertEquals(listOf("7"), out.raw)
        assertEquals(mapOf("x" to "7"), env.snapshot())
    }

    @Test
    fun `concatena string+string en println`() {
        val env = Environment()
        val out = BufferOutput()
        val exec = Executor(env, out, TestIO.empty)

        val prog =
            listOf<StmtIR>(
                decl("s", RType.STRING, str("hola")),
                print(plus(id("s"), str("2025"))),
            )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("hola2025"), out.lines)
        assertEquals(listOf("hola2025"), out.raw)
        assertEquals(mapOf("s" to "hola"), env.snapshot())
    }

    @Test
    fun `string + number en println concatena`() {
        val env = Environment()
        val out = mutableListOf<String>()
        val exec = Executor(env, { text -> out += text }, TestIO.empty)

        val prog =
            listOf<StmtIR>(
                decl("s", RType.STRING, str("hola")),
                print(plus(id("s"), num(2025.0))),
            )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("hola2025"), out)
    }

    @Test
    fun `type mismatch en declaracion - falla`() {
        val env = Environment()
        val exec = Executor(env, {}, TestIO.empty)

        val prog =
            listOf<StmtIR>(
                decl("s", RType.STRING, num(10.0)),
            )

        val ex =
            assertThrows(IllegalArgumentException::class.java) {
                prog.forEach { it.accept(exec) }
            }
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }

    @Test
    fun `declaracion sin inicializador deja variable sin inicializar`() {
        val env = Environment()
        val exec = Executor(env, {}, TestIO.empty)

        decl("s", RType.STRING, null).accept(exec)

        val error = assertThrows(RuntimeError::class.java) { env.read("s") }
        assertTrue(error.message!!.contains("antes de inicializar"))
    }

    @Test
    fun `variable no definida - falla al asignar`() {
        val env = Environment()
        val exec = Executor(env, {}, TestIO.empty)

        val prog = listOf(assign("x", num(3.0)))

        val ex =
            assertThrows(RuntimeError::class.java) {
                prog.forEach { it.accept(exec) }
            }
        assertTrue(ex.message!!.contains("Variable 'x' no definida"))
    }
}
