package org.printscript.interpreter.io

import java.io.BufferedReader
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path

class FileInputProvider(path: Path) : InputProvider, Closeable {
    private val reader: BufferedReader = Files.newBufferedReader(path)

    override fun readLine(prompt: String): String? = reader.readLine()?.trimEnd('\r', '\n')

    override fun close() {
        reader.close()
    }
}
