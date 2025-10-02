package org.printscript.formatter.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.io.File
import kotlin.io.readText

class ConfigJsonReader {
    fun readFromFile(path: String): FormatterConfig {
        val json = File(path).takeIf { it.exists() }?.readText().orEmpty()
        return parse(json)
    }

    fun read(reader: java.io.Reader): FormatterConfig = parse(reader.readText())

    private fun parse(json: String): FormatterConfig {
        if (json.isBlank()) {
            return FormatterConfig(braceStyle = BraceStyle.SAME_LINE)
        }

        val element = JsonParser.parseString(json)
        if (!element.isJsonObject) {
            return FormatterConfig(braceStyle = BraceStyle.SAME_LINE)
        }

        return element.asJsonObject.toFormatterConfig()
    }
}

private fun JsonObject.toFormatterConfig(): FormatterConfig {
    val defaults = FormatterConfig(braceStyle = BraceStyle.SAME_LINE)

    val singleSpace = optBoolean("mandatory-single-space-separation")

    var spaceBeforeColon = defaults.spaceBeforeColon
    var spaceAfterColon = defaults.spaceAfterColon
    var spaceAroundEquals = defaults.spaceAroundEquals
    var spaceAroundOperators = defaults.spaceAroundOperators

    if (singleSpace == true) {
        spaceBeforeColon = true
        spaceAfterColon = true
        spaceAroundEquals = true
        spaceAroundOperators = true
    }

    optBoolean(
        "spaceBeforeColon",
        "spaceBeforeTypeColon",
        "spaceBeforeType",
        "enforce-spacing-before-colon-in-declaration",
    )?.let { spaceBeforeColon = it }

    optBoolean(
        "spaceAfterColon",
        "spaceAfterTypeColon",
        "spaceAfterType",
        "enforce-spacing-after-colon-in-declaration",
    )?.let { spaceAfterColon = it }

    optBoolean(
        "spaceAroundEquals",
        "spaceAroundAssignment",
        "spaceAroundAssign",
        "enforce-spacing-around-equals",
        "enforce-spacing-around-assignment",
        "mandatory-spacing-around-equals",
    )?.let { spaceAroundEquals = it }

    optBooleanNegated(
        "enforce-no-spacing-around-equals",
        "enforce-no-spacing-around-assignment",
    )?.let { spaceAroundEquals = it }

    optBoolean(
        "spaceAroundOperators",
        "spaceAroundOps",
        "spaceAroundBinaryOperators",
        "mandatory-space-surrounding-operations",
    )?.let { spaceAroundOperators = it }

    val lineJumpBeforePrintln =
        optInt(
            "lineJumpBeforePrintln",
            "printlnLeadingLineJumps",
            "line-breaks-after-println",
        ) ?: defaults.lineJumpBeforePrintln

    val lineJumpAfterSemicolon =
        optBoolean(
            "lineJumpAfterSemicolon",
            "newlineAfterSemicolon",
            "lineBreakAfterStatement",
            "mandatory-line-break-after-statement",
        ) ?: defaults.lineJumpAfterSemicolon

    val indentSize =
        optInt(
            "indentSize",
            "indent",
            "indentSpaces",
            "indent-inside-if",
        ) ?: defaults.indentSize

    var braceStyle = optBraceStyle() ?: defaults.braceStyle

    optBoolean("if-brace-same-line", "brace-same-line")?.let {
        braceStyle = if (it) BraceStyle.SAME_LINE else BraceStyle.NEXT_LINE
    }

    optBoolean("if-brace-below-line", "if-brace-next-line", "if-brace-new-line", "brace-below-line", "brace-next-line")
        ?.let { braceStyle = if (it) BraceStyle.NEXT_LINE else BraceStyle.SAME_LINE }

    return FormatterConfig(
        spaceBeforeColon = spaceBeforeColon,
        spaceAfterColon = spaceAfterColon,
        spaceAroundEquals = spaceAroundEquals,
        spaceAroundOperators = spaceAroundOperators,
        lineJumpBeforePrintln = lineJumpBeforePrintln,
        lineJumpAfterSemicolon = lineJumpAfterSemicolon,
        indentSize = indentSize,
        braceStyle = braceStyle,
    )
}

private fun JsonObject.optBoolean(vararg keys: String): Boolean? {
    for (key in keys) {
        if (!has(key)) continue
        val primitive = get(key)?.asPrimitiveOrNull() ?: continue
        val bool = primitive.asFlexibleBoolean()
        if (bool != null) {
            return bool
        }
    }
    return null
}

private fun JsonObject.optBooleanNegated(vararg keys: String): Boolean? {
    for (key in keys) {
        if (!has(key)) continue
        val primitive = get(key)?.asPrimitiveOrNull() ?: continue
        val bool = primitive.asFlexibleBoolean()
        if (bool != null) {
            return !bool
        }
    }
    return null
}

private fun JsonObject.optInt(vararg keys: String): Int? {
    for (key in keys) {
        if (!has(key)) continue
        val primitive = get(key)?.asPrimitiveOrNull() ?: continue
        if (primitive.isNumber) {
            return primitive.asInt
        }
        if (primitive.isString) {
            primitive.asString.trim().toIntOrNull()?.let { return it }
        }
    }
    return null
}

private fun JsonObject.optBraceStyle(): BraceStyle? {
    val keys =
        listOf(
            "braceStyle",
            "bracePosition",
            "ifBraceStyle",
            "ifBracePosition",
            "ifBraceSameLine",
            "if-brace-same-line",
            "braceSameLine",
            "braceOnSameLine",
            "braceOnNewLine",
            "braceBelowStatement",
            "braceBelow",
            "braceNextLine",
            "ifBraceNextLine",
            "ifBraceNewLine",
            "if-brace-next-line",
            "if-brace-new-line",
            "braceBelowLine",
            "brace-next-line",
            "brace-below-line",
        )

    for (key in keys) {
        if (!has(key)) continue
        val primitive = get(key)?.asPrimitiveOrNull() ?: continue
        val keyLower = key.lowercase()

        val bool = primitive.asFlexibleBoolean()
        if (bool != null) {
            return when {
                keyLower.contains("newline") || keyLower.contains("below") || keyLower.contains("nextline") ->
                    if (bool) BraceStyle.NEXT_LINE else BraceStyle.SAME_LINE

                keyLower.contains("sameline") -> if (bool) BraceStyle.SAME_LINE else BraceStyle.NEXT_LINE
                else -> if (bool) BraceStyle.SAME_LINE else BraceStyle.NEXT_LINE
            }
        }

        if (primitive.isString) {
            val normalized = primitive.asString.lowercase().replace("[^a-z]".toRegex(), "")
            return when (normalized) {
                "sameline", "inline", "same" -> BraceStyle.SAME_LINE
                "nextline", "newline", "below", "under", "next" -> BraceStyle.NEXT_LINE
                else -> null
            }
        }
    }
    return null
}

private fun JsonElement.asPrimitiveOrNull(): JsonPrimitive? = this as? JsonPrimitive

private fun JsonPrimitive.asFlexibleBoolean(): Boolean? =
    when {
        isBoolean -> asBoolean
        isString -> asString.trim().toBooleanStrictOrNull()
        isNumber -> asInt != 0
        else -> null
    }
