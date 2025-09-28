package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.ConstDeclIR
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.ReadEnv
import org.printscript.interpreter.ir.ReadInput
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.interpreter.util.TestIO
import org.printscript.interpreter.util.num
import org.printscript.interpreter.util.plus
import org.printscript.interpreter.util.slash
import org.printscript.interpreter.util.star
import org.printscript.interpreter.util.str

class EvaluatorTest {
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
    fun `string + number concatena - hola + 2025 = hola2025`() {
        val v = plus(str("hola"), num(2025.0)).accept(Evaluator(Environment(), TestIO.empty))
        assertEquals(Value.Str("hola2025"), v)
    }

    @Test
    fun `division por cero - lanza RuntimeError`() {
        val env = Environment()
        val ev = Evaluator(env, TestIO.empty)
        val expr = slash(num(1.0), num(0.0))

        assertThrows(RuntimeError::class.java) { expr.accept(ev) }
    }

    @Test
    fun `if verdadero ejecuta rama then`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        env.declare("x", RType.NUMBER, null)
        val stmt =
            IfIR(
                condition = BoolLit(true),
                thenBranch = listOf(AssignIR("x", NumLit(1.0))),
                elseBranch = listOf(AssignIR("x", NumLit(2.0))),
            )
        stmt.accept(executor)
        assertEquals(Value.Num(1.0), env.read("x"))
    }

    @Test
    fun `if falso ejecuta rama else`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        env.declare("x", RType.NUMBER, null)
        val stmt =
            IfIR(
                condition = BoolLit(false),
                thenBranch = listOf(AssignIR("x", NumLit(1.0))),
                elseBranch = listOf(AssignIR("x", NumLit(2.0))),
            )
        stmt.accept(executor)
        assertEquals(Value.Num(2.0), env.read("x"))
    }

    @Test
    fun `declara variable y asigna valor correcto`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        val decl = DeclIR("y", RType.NUMBER, NumLit(42.0))
        decl.accept(executor)
        assertEquals(Value.Num(42.0), env.read("y"))
    }

    @Test
    fun `declara constante y asigna valor correcto`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        val constDecl = ConstDeclIR("z", RType.STRING, StrLit("constante"))
        constDecl.accept(executor)
        assertEquals(Value.Str("constante"), env.read("z"))
    }

    @Test
    fun `print imprime valor numerico`() {
        var output = ""
        val env = Environment()
        // OutputProvider que acumula en 'output'
        val executor = Executor(env, { s: String -> output += s }, TestIO.empty)
        val printStmt = PrintIR(NumLit(7.0))
        printStmt.accept(executor)
        assertEquals("7", output)
    }

    @Test
    fun `declara variable con tipo incorrecto lanza error`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        val decl = DeclIR("x", RType.NUMBER, StrLit("no es número"))
        assertThrows(IllegalArgumentException::class.java) { decl.accept(executor) }
    }

    @Test
    fun `asigna valor a variable existente`() {
        val env = Environment()
        // OutputProvider vacío para test
        val executor = Executor(env, { }, TestIO.empty)
        env.declare("a", RType.NUMBER, Value.Num(1.0))
        val assign = AssignIR("a", NumLit(99.0))
        assign.accept(executor)
        assertEquals(Value.Num(99.0), env.read("a"))
    }

    @Test
    fun `readInput devuelve el valor del input provider`() {
        val env = Environment()
        val io = IOContext(QueueInputProvider(listOf("Ada")), MapEnvProvider(emptyMap()))
        val ev = Evaluator(env, io)
        val expr = ReadInput(StrLit("Nombre:"))
        val v = expr.accept(ev)
        assertEquals(Value.Str("Ada"), v)
    }

    @Test
    fun `readEnv devuelve el valor del env provider`() {
        val env = Environment()
        val io = IOContext.systemDefault()
        val ev = Evaluator(env, io)
        val expr = ReadEnv(StrLit("USER"))
        val v = expr.accept(ev)
        assertEquals(Value.Str("tais"), v)
    }
}
