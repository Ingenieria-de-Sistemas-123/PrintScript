package org.printscript.linter

import com.google.gson.Gson
import java.io.InputStream

enum class IdentifierStyle { CAMEL_CASE, SNAKE_CASE }

data class LintConfig(
    val identifierStyle: IdentifierStyle = IdentifierStyle.CAMEL_CASE,
) {
    companion object {
        private val gson = Gson()

        fun fromJson(json: String): LintConfig = gson.fromJson(json, LintConfig::class.java)

        fun fromInputStream(stream: InputStream): LintConfig = stream.bufferedReader().use { reader -> fromJson(reader.readText()) }
    }
}
