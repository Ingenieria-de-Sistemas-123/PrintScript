
package org.printscript.formatter.emit

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken

/**
 * Buffer de tokens que conoce el nivel de indentación actual y si estamos al
 * inicio de línea. Mantiene toda la lógica de layout para que los emisores de
 * nodos se concentren en la semántica.
 */
class TokenBuffer(private val config: FormatterConfig) {
    private val _tokens: MutableList<FormatToken> = mutableListOf()
    private var indentLevel = 0
    private var atLineStart = true

    val tokens: List<FormatToken>
        get() = _tokens.toList()

    fun add(token: FormatToken) {
        when (token) {
            is FormatToken.NewLine -> {
                _tokens += token
                atLineStart = true
                return
            }

            is FormatToken.Space -> {
                if (!atLineStart) {
                    _tokens += token
                }
                return
            }

            is FormatToken.Indent -> {
                _tokens += token
                atLineStart = false
                return
            }

            else -> {
                ensureIndent()
                _tokens += token
                atLineStart =
                    token is FormatToken.Semicolon && config.lineJumpAfterSemicolon
            }
        }
    }

    fun addKeyword(text: String) {
        add(FormatToken.Keyword(text))
    }

    fun addSpace() {
        add(FormatToken.Space)
    }

    fun newline(times: Int = 1) {
        if (times <= 0) return
        _tokens += FormatToken.NewLine(times)
        atLineStart = true
    }

    fun markLineStart() {
        atLineStart = true
    }

    fun isAtLineStart(): Boolean = atLineStart

    fun withIndent(block: () -> Unit) {
        indentLevel++
        block()
        indentLevel--
    }

    private fun ensureIndent() {
        if (!atLineStart) {
            return
        }

        if (indentLevel > 0) {
            _tokens += FormatToken.Indent(config.indentSize * indentLevel)
        }
        atLineStart = false
    }
}
