package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class AssignationBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val identifier = handler.consume(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable.")
        val name = identifier.value
        val position = Position(identifier.line, identifier.column)

        handler.consume(TokenType.EQUAL, "Se esperaba '=' en la asignación")

        // recoge tokens hasta ';' respetando paréntesis
        val exprTokens = handler.collectExpressionTokens(with = false)

        // si después de recoger la expresión no hay un ';' explícito, considerarlo error
        if (handler.isAtEnd() || !(handler.peek().type == TokenType.SYNTAX && handler.peek().value == ";")) {
            throw IllegalStateException("Se esperaba ';' al final de la asignación")
        }

        if (exprTokens.isEmpty()) {
            throw ParseException("Se esperaba una expresión en la asignación", identifier.line, identifier.column)
        }

        val expression: ASTNode =
            if (exprTokens.first().type == TokenType.READ_INPUT) {
                // esperar forma: READ_INPUT '(' <inner tokens> ')'
                if (exprTokens.size < 3) {
                    throw ParseException("Llamada a readInput incompleta", exprTokens.first().line, exprTokens.first().column)
                }
                // primer token = READ_INPUT, segundo debe ser '('
                val second = exprTokens.getOrNull(1)
                if (second == null || second.type != TokenType.SYNTAX || second.value != "(") {
                    throw ParseException(
                        "Se esperaba '(' después de 'readInput'",
                        second?.line ?: exprTokens.first().line,
                        second?.column ?: exprTokens.first().column,
                    )
                }
                if (exprTokens.last().type != TokenType.SYNTAX || exprTokens.last().value != ")") {
                    throw ParseException(
                        "Se esperaba ')' al final de la llamada a 'readInput'",
                        exprTokens.last().line,
                        exprTokens.last().column,
                    )
                }

                val innerTokens = exprTokens.subList(2, exprTokens.size - 1)
                if (innerTokens.isEmpty()) {
                    throw ParseException("readInput requiere un argumento", exprTokens.first().line, exprTokens.first().column)
                }

                val innerExpr = ExpressionBuilder(innerTokens).build()
                ReadInputNode(innerExpr, Position(exprTokens.first().line, exprTokens.first().column))
            } else {
                // caso general: delegar a ExpressionBuilder
                ExpressionBuilder(exprTokens).build()
            }

        return AssignationNode(name, expression, position)
    }
}
