package org.printscript.formatter.rules

/**
 * Tokens de FORMATEO (no confundir con tokens del lexer).
 * Los emite el "emitter" a partir del AST del parser.
 * Mantienen semántica (ident, tipo, operadores, etc.) para decidir spacing.
 */
sealed interface FormatToken {
    // contenido
    data class Keyword(val text: String) : FormatToken

    data class Ident(val text: String) : FormatToken

    data class TypeName(val text: String) : FormatToken

    data class NumberLit(val raw: String) : FormatToken

    data class StringLit(val raw: String) : FormatToken

    // símbolos a los que solemos aplicar reglas
    data object Colon : FormatToken

    data object Equals : FormatToken

    // operadores
    enum class OpKind { PLUS, MINUS, STAR, SLASH }

    data class Op(val kind: OpKind) : FormatToken

    // puntuación/estructura
    data object OpenParen : FormatToken

    data object CloseParen : FormatToken

    data object Comma : FormatToken

    data object Semicolon : FormatToken

    // layout
    data class NewLine(val count: Int = 1) : FormatToken

    data class Indent(val spaces: Int) : FormatToken
}
