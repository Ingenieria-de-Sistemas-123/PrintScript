package org.printscript.interpreter.io

import java.io.BufferedReader
import java.io.InputStreamReader

class StdinInputProvider : InputProvider {
    private val br = BufferedReader(InputStreamReader(System.`in`))

    override fun readLine(prompt: String): String? {
        if (prompt.isNotBlank()) {
            println(prompt)
        }
        return br.readLine()
    }
}
