package org.printscript.cli.adapters

import org.printscript.cli.common.LanguageError
import org.printscript.cli.common.Span
import org.printscript.common.Position
import org.printscript.lexer.Lexer
import org.printscript.lexer.exception.LexicalException
import org.printscript.parser.DefaultParser
import org.printscript.parser.ParseException
import org.printscript.parser.node.ASTNode
import java.io.File

class FrontendAdapter(
    private val languageVersion: String = "1.0",
) {
    fun loadSource(path: String): String = File(path).readText()

    fun parseProgram(
        source: String,
        sourcePath: String,
    ): Result<List<ASTNode>> {
        val tokens =
            try {
                val lexer = Lexer()
                val output = lexer.lex(source)
                println("Lexing: 100%")
                output
            } catch (e: LexicalException) {
                return Result.failure(toCliFailure(e.message ?: "Lexical error", e.line, e.column, sourcePath))
            }

        val parser = DefaultParser()
        return try {
            val ast = parser.parse(tokens)
            println("Parsing: 100%")
            Result.success(ast)
        } catch (e: ParseException) {
            Result.failure(toCliFailure(e.message ?: "Parsing error", e.line, e.column, sourcePath))
        }
    }

    private fun toCliFailure(
        msg: String,
        line: Int,
        col: Int,
        path: String,
    ): CliFailure {
        val pos = Position(line, col)
        val err = LanguageError(message = msg, sourcePath = path, span = Span(pos, pos))
        return CliFailure(err)
    }
}

class CliFailure(val error: LanguageError) : RuntimeException(error.message)
