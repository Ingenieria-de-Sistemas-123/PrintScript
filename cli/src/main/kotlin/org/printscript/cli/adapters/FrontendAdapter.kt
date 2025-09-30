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
        val seqResult = parseProgramSequence(source, sourcePath)
        if (seqResult.isFailure) return Result.failure(seqResult.exceptionOrNull()!!)
        val seq = seqResult.getOrThrow()
        return try {
            Result.success(seq.toList())
        } catch (e: LexicalException) {
            Result.failure(toCliFailure(e.message ?: "Lexical error", e.line, e.column, sourcePath))
        } catch (e: ParseException) {
            Result.failure(toCliFailure(e.message ?: "Parsing error", e.line, e.column, sourcePath))
        } catch (e: Exception) {
            Result.failure(toCliFailure(e.message ?: "Parsing error", 1, 1, sourcePath))
        }
    }

    /**
     * Devuelve Sequence<ASTNode> sin materializar.
     * Detecta inmediatamente caracteres de control no permitidos y retorna failure.
     * El resto de errores léxicos/parsing se propagan de forma lazy al consumir la secuencia.
     */
    fun parseProgramSequence(
        source: String,
        sourcePath: String,
    ): Result<Sequence<ASTNode>> {
        // Detección temprana (contrato de test: control chars producen failure inmediato)
        val badChar = source.firstOrNull { isIllegalControl(it) }
        if (badChar != null) {
            return Result.failure(
                toCliFailure(
                    "Lexical error: invalid control character 0x${badChar.code.toString(16)}",
                    1,
                    1,
                    sourcePath,
                ),
            )
        }
        return try {
            val provider = tokenProvider ?: providerFor(languageVersion)
            val lexer = Lexer(provider)
            val tokenStream = lexer.lex(StringReader(source)) // Sequence<Token>
            val parser = DefaultParser()
            val astSeq = parser.parse(tokenStream) // Sequence<ASTNode> (lazy)
            Result.success(astSeq)
        } catch (e: LexicalException) {
            // errores léxicos no cubiertos por control-char (si el lexer lanza temprano)
            Result.failure(toCliFailure(e.message ?: "Lexical error", e.line, e.column, sourcePath))
        } catch (e: Exception) {
            Result.failure(toCliFailure(e.message ?: e.toString(), 1, 1, sourcePath))
        }
    }

    private fun isIllegalControl(c: Char): Boolean {
        val code = c.code
        // Permitimos tab (9), LF (10), CR (13) y cualquier >= 32
        return code < 32 && code != 9 && code != 10 && code != 13
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
