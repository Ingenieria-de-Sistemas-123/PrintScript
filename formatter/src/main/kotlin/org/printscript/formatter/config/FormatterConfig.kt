package org.printscript.formatter.config

/**
 * Configuración para el formatter de PrintScript.
 * Todos los valores tienen defaults documentados.
 * - spaceBeforeColon: Espacio antes de ':' en declaraciones
 * - spaceAfterColon: Espacio después de ':' en declaraciones
 * - spaceAroundEquals: Espacio alrededor de '='
 * - spaceAroundOperators: Espacio alrededor de operadores aritméticos
 * - printLineBreaksAfter: Cantidad de líneas en blanco EXTRA después de cada println (0..2), según TCK.
 * - lineJumpAfterSemicolon: Salto de línea tras ';' para cada sentencia
 * - singleSpaceSeparation: Un espacio entre tokens visibles (incluye antes de '(', ')' y ';')
 * - indentSize: Cantidad de espacios para indentación en bloques.
 * - braceStyle: Estilo de llaves en bloques if/else (solo v1.1).
 */
enum class BraceStyle {
    SAME_LINE, // { en la misma línea que if
    NEXT_LINE, // { en la línea siguiente
}

data class FormatterConfig(
    val spaceBeforeColon: Boolean = false,
    val spaceAfterColon: Boolean = false,
    val spaceAroundEquals: Boolean = false,
    // Nullable flag that indicates the JSON explicitly requested a policy for '=' spacing.
    // null -> no explicit request (preserve original layout when available)
    // true -> enforce spaces around '='
    // false -> enforce no spaces around '='
    val spaceAroundEqualsExplicit: Boolean? = null,
    val spaceAroundOperators: Boolean = false,
    val printLineBreaksAfter: Int = 0,
    val singleSpaceSeparation: Boolean = false,
    val lineJumpAfterSemicolon: Boolean = false,
    val indentSize: Int = 4,
    val braceStyle: BraceStyle = BraceStyle.SAME_LINE,
)
