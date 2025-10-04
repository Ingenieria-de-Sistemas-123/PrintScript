package org.printscript.formatter.render

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.CodeFormatRule
import org.printscript.formatter.rules.FormatToken
import org.printscript.formatter.rules.SpaceAroundColon
import org.printscript.formatter.rules.SpaceAroundEquals
import org.printscript.formatter.rules.SpaceAroundOperator

/**
 * Aplica reglas de formateo a una lista de tokens.
 * Las reglas pueden ser inyectadas para facilitar extensión y testing.
 * Para agregar una nueva regla, impleméntala y pásala en el constructor.
 */
class RuleApplier(
    private val config: FormatterConfig,
    private val rules: List<CodeFormatRule> =
        listOf(
            SpaceAroundEquals(),
            SpaceAroundColon(),
            SpaceAroundOperator(),
            // Agrega aquí nuevas reglas si es necesario
        ),
) {
    fun apply(tokens: List<FormatToken>): String {
        val out = StringBuilder()
        for (i in tokens.indices) {
            val t = tokens[i]
            val ctx = ApplyContext(tokens.getOrNull(i - 1), tokens.getOrNull(i + 1), config, out)
            // Aplica la primera regla que matchee, si existe
            val rule = rules.firstOrNull { it.matches(t) }
            if (rule != null) {
                rule.apply(t, ctx)
            } else {
                // Fallback: renderizado por defecto con los tipos válidos de FormatToken
                when (t) {
                    is FormatToken.NewLine -> repeat(t.count) { out.append('\n') }
                    is FormatToken.Indent -> out.append(" ".repeat(t.spaces))
                    is FormatToken.OpenParen -> out.append('(')
                    is FormatToken.CloseParen -> out.append(')')
                    is FormatToken.OpenBrace -> out.append('{')
                    is FormatToken.CloseBrace -> out.append('}')
                    is FormatToken.Comma -> out.append(", ")
                    is FormatToken.Semicolon -> {
                        out.append(';')
                        if (config.lineJumpAfterSemicolon) out.append('\n')
                    }
                    is FormatToken.Space -> {
                        if (out.isNotEmpty() && out.last() != ' ' && out.last() != '\n') {
                            out.append(' ')
                        }
                    }
                    is FormatToken.Keyword -> out.append(t.text)
                    is FormatToken.Ident -> {
                        // Asegurar espacio entre 'let' y el identificador si no vino un Space explícito
                        val prev = tokens.getOrNull(i - 1)
                        if (prev is FormatToken.Keyword && prev.text == "let") {
                            if (out.isNotEmpty() && out.last() != ' ' && out.last() != '\n') {
                                out.append(' ')
                            }
                        }
                        out.append(t.text)
                    }
                    is FormatToken.TypeName -> out.append(t.text)
                    is FormatToken.NumberLit -> out.append(t.raw)
                    is FormatToken.StringLit -> out.append('"').append(t.raw).append('"')
                    is FormatToken.Op -> {
                        val symbol =
                            when (t.kind) {
                                FormatToken.OpKind.PLUS -> "+"
                                FormatToken.OpKind.MINUS -> "-"
                                FormatToken.OpKind.STAR -> "*"
                                FormatToken.OpKind.SLASH -> "/"
                            }
                        out.append(symbol)
                    }
                    is FormatToken.Colon, is FormatToken.Equals -> {
                        // Estos deberían estar cubiertos por reglas; si llegan aquí, rendereamos literal
                        out.append(
                            when (t) {
                                is FormatToken.Colon -> ":"
                                else -> "="
                            },
                        )
                    }
                }
            }
        }
        return out.toString().trimEnd('\n')
    }
}
