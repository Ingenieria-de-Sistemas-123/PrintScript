package org.printscript.interpreter.io

data class IOContext(
    val input: InputProvider,
    val env: EnvProvider,
)

fun interface InputProvider {
    fun readLine(prompt: String): String?
}

fun interface EnvProvider {
    fun get(name: String): String?
}
