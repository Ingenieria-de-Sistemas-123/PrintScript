package org.printscript.formatter.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser

class FormatterConfigParser {
    fun parse(json: String): FormatterConfig {
        if (json.isBlank()) return FormatterConfig()
        val element = runCatching { JsonParser.parseString(json) }.getOrNull()
        val obj = element?.takeIf { it.isJsonObject }?.asJsonObject ?: return FormatterConfig()
        return obj.toFormatterConfig()
    }
}

internal fun JsonObject.toFormatterConfig(): FormatterConfig {
    val defaults = FormatterConfig()

    // 1) Spacing alrededor de ':' '=' y operadores (direct keys + alias TCK)
    var spaceBeforeColon = this["spaceBeforeColon"]?.asBoolean ?: defaults.spaceBeforeColon
    var spaceAfterColon = this["spaceAfterColon"]?.asBoolean ?: defaults.spaceAfterColon
    var spaceAroundEquals = this["spaceAroundEquals"]?.asBoolean ?: defaults.spaceAroundEquals
    var spaceAroundEqualsExplicit: Boolean? = null
    var spaceBeforeColonExplicit: Boolean? = null
    var spaceAfterColonExplicit: Boolean? = null
    var singleSpaceSeparationExplicit: Boolean? = null
    var spaceAroundOperators = this["spaceAroundOperators"]?.asBoolean ?: defaults.spaceAroundOperators
    var singleSpaceSeparation =
        this["singleSpaceSeparation"]?.asBoolean
            ?: this["enforceSingleSpaceSeparation"]?.asBoolean
            ?: defaults.singleSpaceSeparation

    // Master flag (should not override explicit configurations)
    val singleSpaceMaster =
        optBoolean(
            "enforce-single-space-separation",
            "mandatory-single-space-separation",
        ) == true

    singleSpaceSeparation =
        optBoolean("enforce-single-space-separation", "mandatory-single-space-separation")
            ?: singleSpaceSeparation

    // Track explicit single space separation request
    val singleSpaceProvided =
        hasAny(
            "singleSpaceSeparation",
            "enforceSingleSpaceSeparation",
            "enforce-single-space-separation",
            "mandatory-single-space-separation",
        )
    if (singleSpaceProvided) {
        singleSpaceSeparationExplicit = optBoolean("enforce-single-space-separation", "mandatory-single-space-separation")
            ?: this["singleSpaceSeparation"]?.asBoolean
            ?: this["enforceSingleSpaceSeparation"]?.asBoolean
    }

    // TCK aliases for ':'
    val beforeColonProvided =
        hasAny(
            "spaceBeforeColon",
            "enforce-decl-spacing-before-colon",
            "enforce-spacing-before-colon-in-declaration",
        )
    val afterColonProvided =
        hasAny(
            "spaceAfterColon",
            "enforce-decl-spacing-after-colon",
            "enforce-spacing-after-colon-in-declaration",
        )

    spaceBeforeColon =
        optBoolean(
            "enforce-decl-spacing-before-colon",
            "enforce-spacing-before-colon-in-declaration",
        ) ?: spaceBeforeColon
    spaceAfterColon =
        optBoolean(
            "enforce-decl-spacing-after-colon",
            "enforce-spacing-after-colon-in-declaration",
        ) ?: spaceAfterColon

    // Track explicit colon spacing requests
    if (beforeColonProvided) {
        spaceBeforeColonExplicit = optBoolean(
            "enforce-decl-spacing-before-colon",
            "enforce-spacing-before-colon-in-declaration",
        ) ?: this["spaceBeforeColon"]?.asBoolean
    }
    if (afterColonProvided) {
        spaceAfterColonExplicit = optBoolean(
            "enforce-decl-spacing-after-colon",
            "enforce-spacing-after-colon-in-declaration",
        ) ?: this["spaceAfterColon"]?.asBoolean
    }

    // equals: soporta 'enforce-spacing-around-equals' y su opuesto 'enforce-no-spacing-around-equals'
    val equalsProvided =
        hasAny(
            "spaceAroundEquals",
            "enforce-spacing-around-equals",
            "enforce-no-spacing-around-equals",
        )
    val noEqSpaces = optBoolean("enforce-no-spacing-around-equals")
    spaceAroundEquals =
        when {
            noEqSpaces == true -> false
            else -> optBoolean("enforce-spacing-around-equals") ?: spaceAroundEquals
        }
    // Track explicit intent if any of the equals keys were provided
    if (equalsProvided) {
        spaceAroundEqualsExplicit =
            when {
                noEqSpaces == true -> false
                optBoolean("enforce-spacing-around-equals") == true -> true
                this.has("spaceAroundEquals") -> this["spaceAroundEquals"].asBoolean
                else -> null
            }
    }

    // operadores: alias TCK
    val operatorsProvided =
        hasAny(
            "spaceAroundOperators",
            "enforce-operator-spacing",
            "mandatory-space-surrounding-operations",
        )
    spaceAroundOperators =
        optBoolean("enforce-operator-spacing", "mandatory-space-surrounding-operations")
            ?: spaceAroundOperators

    // Aplicar switch maestro solo si no hubo claves específicas
    if (singleSpaceMaster) {
        if (!beforeColonProvided) spaceBeforeColon = true
        if (!afterColonProvided) spaceAfterColon = true
        if (!equalsProvided) spaceAroundEquals = true
        if (!operatorsProvided) spaceAroundOperators = true
        singleSpaceSeparation = true
    }

    // 2) Line breaks after println (TCK: print/println-*-line-breaks-after, line-breaks-after-println)
    val printLineBreaksAfter =
        pickPrintLineBreaksAfter() // lee print/println-X-line-breaks-after y line-breaks-after-println
            ?: optInt("line-breaks-after-println")
            ?: this["lineJumpBeforePrintln"]?.asInt // compat legacy key
            ?: defaults.printLineBreaksAfter

    // 3) Salto de línea luego de ';' (alias TCK + direct key)
    val lineJumpAfterSemicolon =
        this["lineJumpAfterSemicolon"]?.asBoolean
            ?: optBoolean("line-break-after-statement-enforced", "mandatory-line-break-after-statement")
            ?: defaults.lineJumpAfterSemicolon

    // 4) Indentación para if-blocks: soporta banderas if-indent-inside-X y valores numéricos
    val directIndent = this["indentSize"]?.asInt
    val indentSize =
        pickIfIndentInside() // busca if-indent-inside-2/4/8 (true)
            ?: optInt("indent-inside-if", "indent-size")
            ?: directIndent
            ?: defaults.indentSize

    // 5) Estilo de llaves para if (direct string o banderas booleanas)
    val directBrace =
        when (this["braceStyle"]?.asString?.lowercase()) {
            "same_line", "same-line", "same", "inline" -> BraceStyle.SAME_LINE
            "next_line", "next-line", "below", "new_line", "new-line" -> BraceStyle.NEXT_LINE
            else -> null
        }
    val braceStyle =
        when {
            directBrace != null -> directBrace
            optBoolean("if-brace-same-line", "ifBraceSameLine") == true -> BraceStyle.SAME_LINE
            optBoolean("if-brace-below-line", "braceOnNewLine") == true -> BraceStyle.NEXT_LINE
            else -> defaults.braceStyle
        }

    return FormatterConfig(
        spaceBeforeColon = spaceBeforeColon,
        spaceAfterColon = spaceAfterColon,
        spaceAroundEquals = spaceAroundEquals,
        spaceBeforeColonExplicit = spaceBeforeColonExplicit,
        spaceAfterColonExplicit = spaceAfterColonExplicit,
        spaceAroundEqualsExplicit = spaceAroundEqualsExplicit,
        singleSpaceSeparationExplicit = singleSpaceSeparationExplicit,
        spaceAroundOperators = spaceAroundOperators,
        printLineBreaksAfter = printLineBreaksAfter,
        singleSpaceSeparation = singleSpaceSeparation,
        lineJumpAfterSemicolon = lineJumpAfterSemicolon,
        indentSize = indentSize,
        braceStyle = braceStyle,
    )
}

private fun JsonObject.pickPrintLineBreaksAfter(): Int? {
    // Lee banderas booleanas y elige el mayor valor verdadero
    val pairs =
        listOf(
            // print-*
            "print-0-line-breaks-after" to 0,
            "print-1-line-breaks-after" to 1,
            "print-2-line-breaks-after" to 2,
            // println-*
            "println-0-line-breaks-after" to 0,
            "println-1-line-breaks-after" to 1,
            "println-2-line-breaks-after" to 2,
        )
    val flagged = pairs.filter { (k, _) -> this[k]?.asBoolean == true }.map { it.second }
    val fromNumber = this["line-breaks-after-println"]?.asInt
    return (flagged + listOfNotNull(fromNumber)).maxOrNull()
}

private fun JsonObject.pickIfIndentInside(): Int? {
    val pairs =
        listOf(
            "if-indent-inside-2" to 2,
            "if-indent-inside-4" to 4,
            "if-indent-inside-8" to 8,
        )
    val flagged = pairs.filter { (k, _) -> this[k]?.asBoolean == true }.map { it.second }
    return flagged.maxOrNull()
}

private fun JsonObject.optBoolean(vararg keys: String): Boolean? = keys.firstNotNullOfOrNull { k -> this[k]?.asBoolean }

private fun JsonObject.optInt(vararg keys: String): Int? = keys.firstNotNullOfOrNull { k -> this[k]?.asInt }

private fun JsonObject.hasAny(vararg keys: String): Boolean = keys.any { this.has(it) }
