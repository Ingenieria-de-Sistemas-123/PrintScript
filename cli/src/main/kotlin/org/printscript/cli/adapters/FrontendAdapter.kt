package org.printscript.cli.adapters

import org.printscript.cli.common.LanguageError
import org.printscript.cli.common.Span
import org.printscript.common.Position
import org.printscript.lexer.Lexer
import org.printscript.lexer.exception.LexicalException
import org.printscript.lexer.pattern.PreConfiguredTokens
import org.printscript.lexer.pattern.TokenProvider
import org.printscript.parser.DefaultParser
import org.printscript.parser.ParseException
import org.printscript.parser.node.ASTNode
import java.io.File
import java.io.StringReader

class FrontendAdapter(
    private val languageVersion: String = "1.0",
    private val tokenProvider: TokenProvider? = null,
) {
    fun loadSource(path: String): String = File(path).readText()

    fun parseProgram(
        source: String,
        sourcePath: String,
    ): Result<List<ASTNode>> {
        val provider = tokenProvider ?: providerFor(languageVersion)

        val tokens =
            try {
                val lexer = Lexer(provider)
                val output = lexer.lex(StringReader(source))
                println("Lexing: 100%")
                output
            } catch (e: LexicalException) {
                return Result.failure(toCliFailure(e.message ?: "Lexical error", e.line, e.column, sourcePath))
            } catch (e: Exception) {
                return Result.failure(toCliFailure(e.message ?: "Lexical error", 1, 1, sourcePath))
            }

        val parser = DefaultParser()
        return try {
            val ast = parser.parse(tokens)
            println("Parsing: 100%")
            Result.success(ast)
        } catch (e: ParseException) {
            Result.failure(toCliFailure(e.message ?: "Parsing error", e.line, e.column, sourcePath))
        } catch (e: Exception) {
            Result.failure(toCliFailure(e.message ?: "Parsing error", 1, 1, sourcePath))
        }
    }

    private fun providerFor(version: String): TokenProvider =
        when (version) {
            "1.1" -> PreConfiguredTokens.TOKENS_1_1
            "1.0" -> PreConfiguredTokens.TOKENS_1_0
            else -> error("Unsupported language version '$version'")
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
