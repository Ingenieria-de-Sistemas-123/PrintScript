package org.printscript.formatter.config

import com.google.gson.Gson
import java.io.File

class ConfigJsonReader {
    fun readFromFile(path: String): FormatterConfig {
        val json = File(path).readText()
        return Gson().fromJson(json, FormatterConfig::class.java)
    }
}
