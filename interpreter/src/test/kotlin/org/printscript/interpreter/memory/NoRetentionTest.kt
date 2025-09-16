package org.printscript.interpreter.memory

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StmtIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

@Tag("memory")
class NoRetentionTest {
    private var keepAlive: Any? = null

    @Test
    fun `IR queda elegible para GC luego de ejecutar (phantom)`() {
        val exec = Executor(Environment()) {}

        val (queue, phantom) = buildAndRunProgramPhantom(exec)
        keepAlive = phantom

        val collected = waitEnqueued(queue)
        assertTrue(collected, "El IR sigue referenciado; posible retención")
    }

    private fun buildAndRunProgramPhantom(exec: Executor): Pair<ReferenceQueue<Any>, PhantomReference<Any>> {
        val queue = ReferenceQueue<Any>()
        lateinit var phantom: PhantomReference<Any>

        run {
            val prog =
                ArrayList<StmtIR>(4000).apply {
                    repeat(2000) { i ->
                        add(DeclIR("v$i", RType.NUMBER, NumLit(i.toDouble())))
                    }
                    repeat(2000) { i ->
                        add(PrintIR(IdRef("v$i")))
                    }
                }

            prog.forEach { it.accept(exec) }

            @Suppress("UNCHECKED_CAST")
            phantom = PhantomReference(prog as Any, queue)
        }

        return queue to phantom
    }

    private fun waitEnqueued(
        queue: ReferenceQueue<Any>,
        timeoutMs: Long = 3_000,
    ): Boolean {
        val deadline = System.nanoTime() + timeoutMs * 1_000_000
        while (System.nanoTime() < deadline) {
            System.gc()
            if (queue.poll() != null) return true
            Thread.sleep(20)
        }
        return false
    }
}
