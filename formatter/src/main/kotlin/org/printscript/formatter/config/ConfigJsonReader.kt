package org.printscript.formatter.config

import java.io.File
import java.io.Reader

class ConfigJsonReader(
    private val parser: FormatterConfigParser = FormatterConfigParser(),
) {
    fun readFromFile(path: String): FormatterConfig {
        val file = File(path)
        if (!file.exists()) return FormatterConfig()
        return read(file.readText())
    }

    fun read(reader: Reader): FormatterConfig = read(reader.readText())

    fun read(json: String): FormatterConfig = parser.parse(json)
}
