package org.printscript.formatter.config

/**
 * Flags de estilo (no nullables) con defaults razonables.
 * Se pueden cargar desde JSON con Gson (los campos ausentes toman el default).
 */
data class FormatterConfig(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = true,
    val spaceAroundEquals: Boolean = true,
    val lineJumpBeforePrintln: Int = 0,
    val lineJumpAfterSemicolon: Boolean = true,
    val spaceAroundOperators: Boolean = true,
    val indentSize: Int = 4
)
