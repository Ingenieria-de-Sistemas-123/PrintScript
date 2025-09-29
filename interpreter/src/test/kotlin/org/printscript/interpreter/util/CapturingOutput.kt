package org.printscript.interpreter.util

import org.printscript.interpreter.io.OutputProvider

class CapturingOutput : OutputProvider {
    private val buffer = mutableListOf<String>()

    val raw: List<String>
        get() = buffer.toList()

    val lines: List<String>
        get() = buffer.map { it.removeSuffix("\n") }

    override fun println(text: String) {
        buffer += text
    }
}
