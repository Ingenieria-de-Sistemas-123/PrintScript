package org.printscript.interpreter.io

import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayDeque

class FileInputProvider(path: Path) : InputProvider {
    private val queue: ArrayDeque<String> =
        ArrayDeque(
            Files.readAllLines(path).map { it.trimEnd('\r', '\n') },
        )

    override fun readLine(prompt: String): String? = queue.pollFirst()
}
