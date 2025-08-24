package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.testutil.*
import org.printscript.interpreter.ir.*
import org.printscript.interpreter.io.OutputProvider

class ExecutorTest {
    //sentencias: decl, assign, print

    @Test
    fun `declarar numero y printear resultado`() {
        val env = Environment()
        val out = object : OutputProvider {
            val lines = mutableListOf<String>()
            override fun println(text: String) { lines += text }
        }
        val exec = Executor(env, out)

        val prog = listOf<StmtIR>(
            decl("x", RType.NUMBER, plus(num(1.0), star(num(2.0), num(3.0)))), // 7
            print(id("x"))
        )

        prog.forEach { it.accept(exec) }

        assertEquals(listOf("7"), out.lines)
        assertEquals(mapOf("x" to "7"), env.snapshot())
    }

    @Test
    fun `type mismatch en declaracion - falla`() {
        val env = Environment()
        val out = OutputProvider { }
        val exec = Executor(env, out)

        val prog = listOf<StmtIR>(
            decl("s", RType.STRING, num(10.0))
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            prog.forEach { it.accept(exec) }
        }
        // El mensaje lo arma require(...) en Executor.visitDecl
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }

    @Test
    fun `variable no definida - falla al asignar`() {
        val env = Environment()
        val out = OutputProvider { }
        val exec = Executor(env, out)

        val prog = listOf(assign("x", num(3.0)))

        val ex = assertThrows(RuntimeError::class.java) {
            prog.forEach { it.accept(exec) }
        }
        assertTrue(ex.message!!.contains("Variable 'x' no definida"))
    }
}
