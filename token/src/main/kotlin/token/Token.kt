package org.printscript.token

data class Token(
    val type: TokenType,
    val value: String,
    val line: Int,
    val column: Int,
)
