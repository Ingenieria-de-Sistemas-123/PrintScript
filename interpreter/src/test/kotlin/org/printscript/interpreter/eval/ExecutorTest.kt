package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.testutil.*
import org.printscript.interpreter.ir.*

class ExecutorTest {

    @Test
    fun `declara x y lo imprime`() {
        val env = Environment()
        val out = object : OutputProvider {
            val lines = mutableListOf<String>()
            override fun println(text: String) { lines += text }
        }
        val exec = Executor(env, out)

        //x=1+2*3
        val prog = listOf<StmtIR>(
            decl("x", RType.NUMBER, plus(num(1.0), star(num(2.0), num(3.0)))), // 7
            print(id("x"))
        )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("7"), out.lines)
        assertEquals(mapOf("x" to "7"), env.snapshot())
    }

    @Test
    fun `concatena string+string en println`() {
        val env = Environment()
        val out = object : OutputProvider {
            val lines = mutableListOf<String>()
            override fun println(text: String) { lines += text }
        }
        val exec = Executor(env, out)

        val prog = listOf<StmtIR>(
            decl("s", RType.STRING, str("hola")),
            print(plus(id("s"), str("2025")))
        )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("hola2025"), out.lines)
        assertEquals(mapOf("s" to "hola"), env.snapshot())
    }

    @Test
    fun `string + number en println falla (tipado estricto en '+')`() {
        val env = Environment()
        val exec = Executor(env) { /* no-op */ }

        val prog = listOf<StmtIR>(
            decl("s", RType.STRING, str("hola")),
            print(plus(id("s"), num(2025.0)))
        )

        val ex = assertThrows(RuntimeError::class.java) {
            prog.forEach { it.accept(exec) }
        }
        assertTrue(ex.message!!.contains("Operador '+' no definido"))
    }

    @Test
    fun `type mismatch en declaracion - falla`() {
        val env = Environment()
        val exec = Executor(env) { /* no-op */ }

        val prog = listOf<StmtIR>(
            decl("s", RType.STRING, num(10.0))
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            prog.forEach { it.accept(exec) }
        }
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }

    @Test
    fun `variable no definida - falla al asignar`() {
        val env = Environment()
        val exec = Executor(env) { }

        val prog = listOf(assign("x", num(3.0)))

        val ex = assertThrows(RuntimeError::class.java) {
            prog.forEach { it.accept(exec) }
        }
        assertTrue(ex.message!!.contains("Variable 'x' no definida"))
    }
}
