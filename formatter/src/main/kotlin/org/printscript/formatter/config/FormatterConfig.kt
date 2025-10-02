package org.printscript.formatter.config

enum class BraceStyle {
    SAME_LINE,
    NEXT_LINE,
}

data class FormatterConfig(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = true,
    val spaceAroundEquals: Boolean = true,
    val spaceAroundOperators: Boolean = true,
    val lineJumpBeforePrintln: Int = 0,
    val lineJumpAfterSemicolon: Boolean = true,
    val indentSize: Int = 4,
    val braceStyle: BraceStyle = BraceStyle.SAME_LINE,
)
