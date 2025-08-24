package org.printscript.interpreter.memory

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.ir.*
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

@Tag("memory")
class NoRetentionTest {

    // Mantiene viva la PhantomReference hasta el fin del test (evita GC de la referencia misma)
    private var keepAlive: Any? = null

    //en resumen, este test verifica que el IR no tenga referencias retenidas
    // Verifica que el IR usado en una ejecución grande sea recolectado
    // usando PhantomReference y ReferenceQueue.

    // Construye y ejecuta un programa grande dentro de un scope
    // que crea una PhantomReference al IR antes de salir del scope.
    // Luego de salir del scope, fuerza GC y espera a que la PhantomReference
    // sea encolada, lo que indica que el IR fue recolectado.
    // Si no se encola en un tiempo razonable, es probable que haya retención
    // de referencias al IR en alguna parte del Executor/Evaluator/Environment.

    @Test
    fun `IR queda elegible para GC luego de ejecutar (phantom)`() {
        val exec = Executor(Environment(), OutputProvider { })

        val (queue, phantom) = buildAndRunProgramPhantom(exec)
        keepAlive = phantom  // referencia fuerte para que no se GC la phantom

        val collected = waitEnqueued(queue) // usa valor por defecto (3s)
        assertTrue(collected, "El IR sigue referenciado; posible retención")
    }

    private fun buildAndRunProgramPhantom(
        exec: Executor
    ): Pair<ReferenceQueue<Any>, PhantomReference<Any>> {
        val queue = ReferenceQueue<Any>()
        lateinit var phantom: PhantomReference<Any>

        // Aisla 'prog' en este stack frame: al salir, no quedan refs fuertes
        run {
            val prog = ArrayList<StmtIR>(4000).apply {
                // let v<i>: number = i;
                repeat(2000) { i ->
                    add(DeclIR("v$i", RType.NUMBER, NumLit(i.toDouble())))
                }
                // println(v<i>);
                repeat(2000) { i ->
                    add(PrintIR(IdRef("v$i")))
                }
            }

            // Ejecutar el programa
            prog.forEach { it.accept(exec) }

            // Creamos la PhantomReference ANTES de salir del scope
            @Suppress("UNCHECKED_CAST")
            phantom = PhantomReference(prog as Any, queue)
        }

        return queue to phantom
    }

    /**
     * Espera hasta que el GC encole la referencia (no determinístico).
     * Reintenta con GC + sleep; 3s suele ser suficiente en CI.
     */
    private fun waitEnqueued(
        queue: ReferenceQueue<Any>,
        timeoutMs: Long = 3_000
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