package org.printscript.lexer.exception

class LexicalException(
    message: String,
    val line: Int,
    val column: Int,
) : RuntimeException(message)
