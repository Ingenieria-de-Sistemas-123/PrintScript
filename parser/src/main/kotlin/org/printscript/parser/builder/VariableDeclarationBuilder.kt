package org.printscript.parser.builder

import com.printscript.parser.builder.ExpressionBuilder
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.*
import org.printscript.token.Token
import org.printscript.token.TokenType


class VariableDeclarationBuilder(private val line: List<Token>) : Builder {

    override fun build(): ASTNode {
        val handler = TokenHandler(line)

        val first = handler.advance()
        val isConst = when (first.type) {
            TokenType.CONST -> true
            TokenType.LET   -> false
            else -> throw ParseException("Se esperaba 'let' o 'const'.", first.line, first.column)
        }

        val identifier = handler.consume(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable.")
        val name = identifier.value
        val position = Position(identifier.line, identifier.column)

        val colon = handler.consume(TokenType.SYNTAX, "Se esperaba ':' después del nombre de la variable.")
        if (colon.value != ":") throw ParseException("Se esperaba ':' pero encontré '${colon.value}'", colon.line, colon.column)

        val typeTok = handler.peek()
        val type = when (typeTok.type) {
            TokenType.NUMBER_TYPE  -> { handler.advance(); "number" }
            TokenType.STRING_TYPE  -> { handler.advance(); "string" }
            TokenType.BOOLEAN_TYPE -> { handler.advance(); "boolean" }
            else -> throw ParseException("Se esperaba el tipo de la variable (number|string|boolean).", typeTok.line, typeTok.column)
        }

        if (handler.peek().type == TokenType.SYNTAX && handler.peek().value == ";") {
            if (isConst) throw ParseException("La constante '$name' requiere un inicializador", identifier.line, identifier.column)
            handler.consume(TokenType.SYNTAX, "Se esperaba ';' después de la declaración.")
            // tu VariableDeclarationNode espera un expr; usamos expresión vacía según tu estilo
            return VariableDeclarationNode(name, type, ExpressionBuilder(emptyList()).build(), position)
        }

        handler.consume(TokenType.EQUAL, "Se esperaba '=' después del nombre de la variable.")
        val exprTokens = handler.collectExpressionTokens(false)
        val semi = handler.consume(TokenType.SYNTAX, "Se esperaba ';' después de la declaración.")
        if (semi.value != ";") throw ParseException("Se esperaba ';' pero encontré '${semi.value}'", semi.line, semi.column)

        val expr = resolveExpression(exprTokens.firstOrNull(), exprTokens)

        return if (isConst) {
            ConstantDeclarationNode(name, type, expr, position)
        } else {
            VariableDeclarationNode(name, type, expr, position)
        }
    }

    private fun resolveExpression(first: Token?, tokens: List<Token>): ASTNode =
        when (first?.type) {
            TokenType.READ_INPUT -> ReadInputBuilder(tokens).build()
            TokenType.READ_ENV   -> ReadEnvBuilder(tokens).build()
            else                 -> ExpressionBuilder(tokens).build()
        }
}
