package org.printscript.formatter.interfaces

/**
 * Tokens de FORMATEO (no son los del lexer).
 * Los emite el VISITOR del AST y conservan la semántica:
 * - distinguen identificadores, literales, operadores, etc.
 * - permiten a las reglas decidir spacing sin tocar Strings ni romper el sentido.
 */
sealed interface FormatToken {
    // Palabras y atómicos “de contenido”
    data class Keyword(val text: String) : FormatToken
    data class Ident(val text: String) : FormatToken
    data class TypeName(val text: String) : FormatToken
    data class NumberLit(val raw: String) : FormatToken
    data class StringLit(val raw: String) : FormatToken

    // Símbolos que suelen tener reglas de espacios
    data object Colon : FormatToken
    data object Equals : FormatToken

    // Operadores binarios/unarios (el contexto prev/next ayuda a decidir)
    enum class OpKind { PLUS, MINUS, STAR, SLASH }
    data class Op(val kind: OpKind) : FormatToken

    // Puntuación / estructura
    data object OpenParen : FormatToken
    data object CloseParen : FormatToken
    data object Comma : FormatToken
    data object Semicolon : FormatToken

    // Control de layout
    data class NewLine(val count: Int = 1) : FormatToken
    data class Indent(val spaces: Int) : FormatToken
}