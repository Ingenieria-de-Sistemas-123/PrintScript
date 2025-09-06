package org.printscript.parser

class ParseException(
    message: String,
    val line: Int,
    val column: Int,
) : RuntimeException(message)
