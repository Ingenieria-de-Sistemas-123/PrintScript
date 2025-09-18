package org.printscript.runner

import com.printscript.lexer.Lexer
import com.printscript.parser.Parser
import java.io.InputStream

class Runner {
    private val parser = Parser()
    fun run(inputStream: InputStream, version: String) {
        val readerIterator = ReaderIterator().getLineIterator(inputStream)
        val tokens = Lexer(version).tokenize(readerIterator)
        // Procesar tokens con el parser y ejecutar lógica
    }
}
