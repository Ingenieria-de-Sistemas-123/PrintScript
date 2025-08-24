package org.printscript.interpreter.runtime

// Errores de tiempo de ejec. del interpreter (division por 0, acceso a variable no definida, etc)
class RuntimeError(message: String) : RuntimeException(message)
