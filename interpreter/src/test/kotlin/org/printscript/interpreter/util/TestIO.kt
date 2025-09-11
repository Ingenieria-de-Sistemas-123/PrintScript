package org.printscript.interpreter.util

import org.printscript.interpreter.io.EnvProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.InputProvider
import java.util.ArrayDeque

object TestIO {
    val empty =
        IOContext(
            input = { _ -> null },
            env = { _ -> null },
        )
}

class QueueInputProvider(values: List<String>) : InputProvider {
    private val q = ArrayDeque(values)

    override fun readLine(prompt: String): String? = q.pollFirst()
}

class MapEnvProvider(private val map: Map<String, String>) : EnvProvider {
    override fun get(name: String): String? = map[name]
}
