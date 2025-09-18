package org.printscript.runner

import org.printscript.lexer.Lexer
import org.printscript.parser.Parser
import java.io.InputStream

class Runner {
    fun run(inputStream: InputStream, version: String) {
        val readerIterator = ReaderIterator().getLineIterator(inputStream)
        // Procesar tokens con el parser y ejecutar lógica
    }
}
