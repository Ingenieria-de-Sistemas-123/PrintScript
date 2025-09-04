package org.printscript.interpreter.util

import org.printscript.interpreter.io.OutputProvider

class BufferOutput : OutputProvider {
    val lines = mutableListOf<String>()
    override fun println(text: String) { lines += text }
}
