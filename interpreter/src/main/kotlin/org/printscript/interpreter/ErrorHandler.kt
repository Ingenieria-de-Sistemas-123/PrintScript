package org.printscript.interpreter

fun interface ErrorHandler {
    fun reportError(message: String)
}

object NoopErrorHandler : ErrorHandler {
    override fun reportError(message: String) {
        // no-op
    }
}
