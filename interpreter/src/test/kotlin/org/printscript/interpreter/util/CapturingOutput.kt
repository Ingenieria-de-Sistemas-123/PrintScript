package org.printscript.interpreter.util

import org.printscript.interpreter.io.OutputProvider

class CapturingOutput : OutputProvider {
    val lines = mutableListOf<String>()

    override fun println(s: String) {
        lines += s
    }
}
