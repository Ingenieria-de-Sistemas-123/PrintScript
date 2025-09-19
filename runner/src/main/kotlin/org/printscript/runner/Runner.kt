package org.printscript.runner

import java.io.InputStream

//TODO: Implementar la clase Runner
class Runner {
    fun run(
        inputStream: InputStream,
        version: String,
    ) {
        val readerIterator = ReaderIterator().getLineIterator(inputStream)
        // Procesar tokens con el parser y ejecutar lógica
    }
}
