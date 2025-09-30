package org.printscript.linter

import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader

object LintConfigLoader {
    fun load(stream: InputStream?): LintConfig {
        if (stream == null) return LintConfig()
        return try {
            val el = JsonParser.parseReader(InputStreamReader(stream))
            if (!el.isJsonObject) return LintConfig()
            val obj = el.asJsonObject
            val style = parseIdentifierStyle(obj.get("identifier_format")?.asString)
            LintConfig(identifierStyle = style)
        } catch (_: Exception) {
            LintConfig()
        }
    }

    private fun parseIdentifierStyle(raw: String?): IdentifierStyle? {
        if (raw == null) return null
        val norm = raw.lowercase().replace(Regex("[ _-]"), "")
        return when (norm) {
            "camelcase" -> IdentifierStyle.CAMEL_CASE
            "snakecase" -> IdentifierStyle.SNAKE_CASE
            else -> null
        }
    }
}
