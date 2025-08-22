package org.printscript.interpreter.io

/* imprime a stdout */
class DefaultOutputProvider : OutputProvider {
    override fun println(text: String) = kotlin.io.println(text)
}
