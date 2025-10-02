package org.printscript.formatter.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.io.File

class ConfigJsonReader {
    fun readFromFile(path: String): FormatterConfig {
        val json = File(path).readText()
        if (json.isBlank()) {
            return FormatterConfig(braceStyle = BraceStyle.SAME_LINE)
        }

        val element = JsonParser.parseString(json)
        if (!element.isJsonObject) {
            return FormatterConfig(braceStyle = BraceStyle.SAME_LINE)
        }

        val obj = element.asJsonObject
        val defaults = FormatterConfig(braceStyle = BraceStyle.SAME_LINE)

        return FormatterConfig(
            spaceBeforeColon = obj.optBoolean("spaceBeforeColon", "spaceBeforeTypeColon", "spaceBeforeType") ?: defaults.spaceBeforeColon,
            spaceAfterColon = obj.optBoolean("spaceAfterColon", "spaceAfterTypeColon", "spaceAfterType") ?: defaults.spaceAfterColon,
            spaceAroundEquals =
                obj.optBoolean(
                    "spaceAroundEquals",
                    "spaceAroundAssignment",
                    "spaceAroundAssign",
                ) ?: defaults.spaceAroundEquals,
            spaceAroundOperators =
                obj.optBoolean(
                    "spaceAroundOperators",
                    "spaceAroundOps",
                    "spaceAroundBinaryOperators",
                ) ?: defaults.spaceAroundOperators,
            lineJumpBeforePrintln =
                obj.optInt(
                    "lineJumpBeforePrintln",
                    "printlnLeadingLineJumps",
                ) ?: defaults.lineJumpBeforePrintln,
            lineJumpAfterSemicolon =
                obj.optBoolean(
                    "lineJumpAfterSemicolon",
                    "newlineAfterSemicolon",
                    "lineBreakAfterStatement",
                ) ?: defaults.lineJumpAfterSemicolon,
            indentSize = obj.optInt("indentSize", "indent", "indentSpaces") ?: defaults.indentSize,
            braceStyle = obj.optBraceStyle() ?: defaults.braceStyle,
        )
    }
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
            "braceSameLine",
            "braceOnSameLine",
            "braceOnNewLine",
            "braceBelowStatement",
            "braceBelow",
            "braceNextLine",
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

private fun JsonPrimitive.asFlexibleBoolean(): Boolean? =
    when {
        isBoolean -> asBoolean
        isNumber -> asInt != 0
        isString ->
            when (asString.trim().lowercase()) {
                "true", "1", "yes", "y", "on" -> true
                "false", "0", "no", "n", "off" -> false
                else -> null
            }
        else -> null
    }

private fun JsonElement?.asPrimitiveOrNull(): JsonPrimitive? = if (this is JsonPrimitive) this else null
