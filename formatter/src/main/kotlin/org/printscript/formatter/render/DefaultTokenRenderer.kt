package org.printscript.formatter.render

import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.FormatToken
import org.printscript.formatter.rules.trimTrailingSpaces

/**
 * Renderizador por defecto para todos los tokens que no tienen una regla
 * específica. Mantiene la lógica previa de salida literal y decisiones de
 * espacios mínimos.
 */
class DefaultTokenRenderer : FormatTokenRenderer {
    override fun render(
        token: FormatToken,
        ctx: ApplyContext,
    ) {
        when (token) {
            is FormatToken.NewLine -> {
                repeat(token.count) {
                    ctx.out.trimTrailingSpaces()
                    ctx.out.append('\n')
                }
            }
            is FormatToken.Indent -> ctx.out.append(" ".repeat(token.spaces))
            is FormatToken.OpenParen -> ctx.out.append('(')
            is FormatToken.CloseParen -> ctx.out.append(')')
            is FormatToken.OpenBrace -> ctx.out.append('{')
            is FormatToken.CloseBrace -> ctx.out.append('}')
            is FormatToken.Comma -> ctx.out.append(", ")
            is FormatToken.Semicolon -> ctx.out.append(';')
            is FormatToken.Space -> appendSpace(ctx)
            is FormatToken.Keyword -> ctx.out.append(token.text)
            is FormatToken.Ident -> ctx.out.append(token.text)
            is FormatToken.TypeName -> ctx.out.append(token.text)
            is FormatToken.NumberLit -> ctx.out.append(token.raw)
            is FormatToken.StringLit -> ctx.out.append('"').append(token.raw).append('"')
            is FormatToken.Op -> ctx.out.append(renderSymbol(token.kind))
            is FormatToken.Colon -> ctx.out.append(':')
            is FormatToken.Equals -> ctx.out.append('=')
        }
    }

    private fun appendSpace(ctx: ApplyContext) {
        if (ctx.out.isNotEmpty()) {
            val last = ctx.out[ctx.out.length - 1]
            if (last != ' ' && last != '\n') {
                ctx.out.append(' ')
            }
        }
    }

    private fun renderSymbol(kind: FormatToken.OpKind): String =
        when (kind) {
            FormatToken.OpKind.PLUS -> "+"
            FormatToken.OpKind.MINUS -> "-"
            FormatToken.OpKind.STAR -> "*"
            FormatToken.OpKind.SLASH -> "/"
        }
}
