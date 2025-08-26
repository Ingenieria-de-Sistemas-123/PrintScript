package org.printscript.formatter.config

data class FormatterConfig(
    val maxLineLength: Int = 100,
    val spacesAroundBinaryOps: Boolean = true,
    val spaceAfterColonInType: Boolean = true,
    val normalizeStringQuotes: Boolean = false, // si true: forzamos comillas dobles
)