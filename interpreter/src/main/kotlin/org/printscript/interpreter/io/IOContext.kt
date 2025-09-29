package org.printscript.interpreter.io

data class IOContext(
    val input: InputProvider,
    val env: EnvProvider,
) {
    companion object {
        fun systemDefault(): IOContext = IOContext(StdinInputProvider(), SystemEnvProvider())
    }
}

fun interface InputProvider {
    fun readLine(prompt: String): String?
}

fun interface EnvProvider {
    fun get(name: String): String?
}
