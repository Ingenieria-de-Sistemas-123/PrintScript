package org.printscript.formatter.config

/**
 * Configuración para el formatter de PrintScript.
 * Todos los valores tienen defaults documentados.
 * - spaceBeforeColon: Espacio antes de ':' en declaraciones
 * - spaceAfterColon: Espacio después de ':' en declaraciones
 * - spaceAroundEquals: Espacio alrededor de '='
 * - spaceAroundOperators: Espacio alrededor de operadores aritméticos
 * - lineJumpBeforePrintln: Saltos de línea que se insertan ANTES de un println (0..2). Alias/compatibilidad con varias claves TCK.
 * - lineJumpAfterSemicolon: Salto de línea tras ';' para cada sentencia
 * - indentSize: Cantidad de espacios para indentación en bloques.
 * - braceStyle: Estilo de llaves en bloques if/else (solo v1.1).
 */
enum class BraceStyle {
    SAME_LINE, // { en la misma línea que if
    NEXT_LINE, // { en la línea siguiente
}

data class FormatterConfig(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = true,
    val spaceAroundEquals: Boolean = true,
    val spaceAroundOperators: Boolean = true,
    val lineJumpBeforePrintln: Int = 0,
    val singleSpaceSeparation: Boolean = false,
    val lineJumpAfterSemicolon: Boolean = true,
    val indentSize: Int = 4,
    val braceStyle: BraceStyle = BraceStyle.SAME_LINE,
)
