package org.printscript.parser.helpers

import org.printscript.parser.ParseException
import org.printscript.token.Token
import org.printscript.token.TokenType

data class TokenHandler(val line: List<Token>) {
    private var currentTokenIndex = 0

    /**
     * Recolecta y devuelve todos los tokens hasta el siguiente ';', respetando paréntesis balanceados.
     * Si 'with' es true, incluye el ';' final en la lista y avanza el índice.
     * Lanza ParseException si los paréntesis no están balanceados o si falta el ';' final cuando 'with' es true.
     */
    fun collectExpressionTokens(with: Boolean): List<Token> {
        val tokens = mutableListOf<Token>()
        var depth = 0

        while (!isAtEnd()) {
            val t = peek()

            // detenerse en ; solo si no estamos dentro de paréntesis
            if (t.type == TokenType.SYNTAX && t.value == ";" && depth == 0) break

            // ajustar profundidad de paréntesis para respetar llamadas/expresiones anidadas
            if (t.type == TokenType.SYNTAX) {
                if (t.value == "(") depth++
                if (t.value == ")") depth--
            }

            tokens += t
            advance()
        }

        if (depth != 0) throwUnbalancedParenthesis()

        if (with) {
            // incluir el ';' final en la lista y avanzar; si no existe, lanzar error con ubicación
            val t = peek()
            if (t.type != TokenType.SYNTAX || t.value != ";") {
                throw ParseException("Se esperaba ';' al final de la instrucción", t.line, t.column)
            }
            tokens += t
            advance()
        }
        return tokens
    }

    /**
     * Recolecta y devuelve todos los tokens dentro de un paréntesis balanceado.
     * Lanza ParseException si los paréntesis no están balanceados.
     * Asume que el token actual es el '(' que inicia la recolección.
     */
    fun collectExpressionTokensInParenthesis(): List<Token> {
        val tokens = mutableListOf<Token>()
        var depth = 1 // ya estamos dentro del '(' que inició la llamada

        while (!isAtEnd()) {
            val t = peek()

            if (t.type == TokenType.SYNTAX) {
                if (t.value == "(") depth++
                if (t.value == ")") {
                    depth--
                    // si cerramos el paréntesis que abrió la recolección, avanzamos y salimos
                    if (depth == 0) {
                        advance()
                        break
                    }
                }
            }

            tokens += t
            advance()
        }

        if (depth != 0) throwUnbalancedParenthesis()

        return tokens
    }

    fun advance(): Token {
        if (!isAtEnd()) currentTokenIndex++
        return previous()
    }

    fun isAtEnd(): Boolean = currentTokenIndex >= line.size

    fun peek(): Token {
        // devolver EOF-like cuando lleguemos al final para evitar excepciones en llamadas a peek desde helpers
        return if (currentTokenIndex < line.size) {
            line[currentTokenIndex]
        } else {
            line.lastOrNull()
                ?: Token(TokenType.EOF, "", 1, 1)
        }
    }

    private fun previous(): Token {
        val idx = (currentTokenIndex - 1).coerceAtLeast(0)
        return line.getOrNull(idx) ?: Token(TokenType.EOF, "", 1, 1)
    }

    fun consume(
        expected: TokenType,
        message: String,
    ): Token {
        val t = peek()
        if (t.type != expected) throw ParseException(message, t.line, t.column)
        advance()
        return previous()
    }

    fun expect(
        expected: TokenType,
        expectedValue: String,
        message: String,
    ): Token {
        val t = peek()
        if (t.type != expected || t.value != expectedValue) throw ParseException(message, t.line, t.column)
        advance()
        return previous()
    }

    /**
     * Valida y consume un punto y coma \(';'\).
     * Lanza ParseException si el token actual no es ';'.
     */
    fun expectSemicolon(): Token {
        val t = peek()
        if (t.type != TokenType.SYNTAX || t.value != ";") {
            throw ParseException("Se esperaba ';' al final de la instrucción", t.line, t.column)
        }
        advance()
        return previous()
    }

    // Helper centralizado para lanzar el error de paréntesis desbalanceados
    private fun throwUnbalancedParenthesis(): Nothing {
        val loc = peekForError()
        throw ParseException("Paréntesis desbalanceados", loc.line, loc.column)
    }

    // Obtiene un token fiable para reportar errores (peek o último token si estamos al final)
    private fun peekForError(): Token {
        return if (!isAtEnd()) peek() else line.lastOrNull() ?: Token(TokenType.EOF, "", 1, 1)
    }
}
