package org.printscript.interpreter.adapter

import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.Binary
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.ExprIR
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.Op
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StmtIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.RType
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.token.TokenType

class ASTtoIR {
    fun transform(program: List<ASTNode>): List<StmtIR> = program.map { toStmt(it) }

    private fun toStmt(n: ASTNode): StmtIR =
        when (n) {
            is DeclarationNode -> {
                val name = n.identifier
                val declaredType = mapType(n.valueType)
                val init = toExpr(n.expression)
                DeclIR(name, declaredType, init)
            }
            is AssignationNode -> {
                val valueExpr = toExpr(n.expression)
                AssignIR(n.variable, valueExpr)
            }
            is PrintStatementNode -> PrintIR(toExpr(n.expression))
            else -> error("Nodo de sentencia no soportado: ${n::class.simpleName}")
        }

    private fun toExpr(n: ASTNode): ExprIR =
        when (n) {
            is LiteralNode<*> -> literalToIr(n)
            is DoubleExpressionNode -> {
                val op = opFrom(n.operator)
                Binary(op, toExpr(n.left), toExpr(n.right))
            }
            else -> error("Nodo de expresión no soportado: ${n::class.simpleName}")
        }

    private fun literalToIr(n: LiteralNode<*>): ExprIR =
        when (n.tokenType) {
            TokenType.NUMBER -> NumLit((n.value as Number).toDouble())
            TokenType.STRING -> StrLit(n.value as String)
            TokenType.IDENTIFIER -> IdRef(n.value as String)
            TokenType.TRUE, TokenType.FALSE -> BoolLit(n.value as Boolean)
            null -> when (val v = n.value) {
                is Number -> NumLit(v.toDouble())
                is String -> if (IDENT_REGEX.matches(v)) IdRef(v) else StrLit(v)
                is Boolean -> BoolLit(v)
                else -> error("Literal no soportado: $v (${v?.let { it::class.simpleName }})")
            }
            else ->
                when (val v = n.value) {
                    is Number -> NumLit(v.toDouble())
                    is String -> StrLit(v)
                    is Boolean -> BoolLit(v)
                    else -> error("Literal no soportado: $v (${v?.let { it::class.simpleName }})")
                }
        }

    private fun mapType(s: String): RType =
        when (s.lowercase()) {
            "number" -> RType.NUMBER
            "string" -> RType.STRING
            "boolean" -> RType.BOOLEAN
            else -> error("Tipo de declaración desconocido '$s'")
        }

    private fun opFrom(opText: String): Op =
        when (opText.trim()) {
            "+" -> Op.PLUS
            "-" -> Op.MINUS
            "*" -> Op.STAR
            "/" -> Op.SLASH
            else -> error("Operador desconocido '$opText'")
        }

    companion object {
        private val IDENT_REGEX = Regex("^[A-Za-z_][A-Za-z0-9_]*$")
    }
}
