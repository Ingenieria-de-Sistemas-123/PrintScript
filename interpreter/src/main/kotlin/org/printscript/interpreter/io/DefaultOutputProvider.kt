package org.printscript.interpreter.io

class DefaultOutputProvider : OutputProvider {
    override fun println(text: String) = kotlin.io.print(text)
}
