package org.printscript.interpreter.io

/* Abstraccion de salida para testear println sin depender de System.out */
fun interface OutputProvider {
    fun println(text: String)
}
