package org.printscript.interpreter.util

import org.printscript.interpreter.ErrorHandler

class CollectingErrorHandler : ErrorHandler {
    val errors = mutableListOf<String>()

    override fun reportError(message: String) {
        errors += message
    }
}
