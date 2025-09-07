package org.printscript.formatter.config

data class FormatterConfig(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = true,
    val spaceAroundEquals: Boolean = true,
    val spaceAroundOperators: Boolean = true,
    val lineJumpBeforePrintln: Int = 0,
    val lineJumpAfterSemicolon: Boolean = true,
    val indentSize: Int = 4,
)
