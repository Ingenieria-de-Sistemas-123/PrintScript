package org.printscript.interpreter.eval

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.ReadEnvIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.CapturingOutput
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.TestIO

class ReadEnvTest {
    @Test fun `readEnv number ok`() {
        val env = Environment()
        val out = CapturingOutput()
        val io = TestIO.empty.copy(env = MapEnvProvider(mapOf("PORT" to "8080")))
        val exec = Executor(env, out, io)
        val prog =
            listOf(
                DeclIR("p", RType.NUMBER, ReadEnvIR("PORT", RType.NUMBER)),
                PrintIR(IdRef("p")),
            )
        exec.exec(prog)
        assertEquals(listOf("8080"), out.lines)
    }

    @Test fun `readEnv missing`() {
        val env = Environment()
        val io = TestIO.empty
        val exec = Executor(env, { }, io)
        val prog = listOf(DeclIR("p", RType.STRING, ReadEnvIR("MISSING", RType.STRING)))
        assertThrows(RuntimeError::class.java) { exec.exec(prog) }
    }

    @Test fun `readEnv boolean invalido lanza error`() {
        val env = Environment()
        val io = TestIO.empty.copy(env = MapEnvProvider(mapOf("FLAG" to "yes"))) // no es true/false
        val exec = Executor(env, { }, io)

        val prog =
            listOf(
                DeclIR("b", RType.BOOLEAN, ReadEnvIR("FLAG", RType.BOOLEAN)),
            )

        assertThrows(RuntimeError::class.java) { exec.exec(prog) }
    }
}
