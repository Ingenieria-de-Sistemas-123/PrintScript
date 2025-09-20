package org.printscript.runner

import org.printscript.lexer.Lexer
import org.printscript.lexer.pattern.TokenProvider
import org.printscript.parser.Parser
import org.printscript.interpreter.Interpreter
import java.io.InputStream
import java.io.InputStreamReader

class Runner(
    private val tokenProvider: TokenProvider,
    private val parser: Parser,
    private val interpreter: Interpreter,
) {
    fun run(inputStream: InputStream) {
        val lexer = Lexer(tokenProvider)
        val reader = InputStreamReader(inputStream)
        val tokens = lexer.lex(reader)
        val ast = parser.parse(tokens)
        interpreter.execute(ast)
    }
}
