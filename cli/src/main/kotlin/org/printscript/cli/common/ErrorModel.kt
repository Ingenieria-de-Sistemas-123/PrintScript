package org.printscript.cli.common

import org.printscript.common.Position

data class Span(
    val start: Position,
    val end: Position,
)

data class LanguageError(
    val message: String,
    val sourcePath: String,
    val span: Span,
) {
    override fun toString(): String = "$sourcePath:${span.start} - ${span.end}: error: $message"
}
