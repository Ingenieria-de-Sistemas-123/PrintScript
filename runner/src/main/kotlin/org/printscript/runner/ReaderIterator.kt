package org.printscript.runner

import java.io.InputStream
import java.util.Scanner

class ReaderIterator {
    fun getLineIterator(inputStream: InputStream): Iterator<String> {
        val scanner = Scanner(inputStream)

        return object : Iterator<String> {
            var nextLine: String? = advance()
            override fun hasNext() = nextLine != null
            override fun next(): String {
                val currentLine = nextLine ?: throw NoSuchElementException()
                nextLine = advance()
                return currentLine
            }
            private fun advance() = if (scanner.hasNextLine()) scanner.nextLine() else null
        }
    }
}
