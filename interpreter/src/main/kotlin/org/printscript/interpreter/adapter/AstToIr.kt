package org.printscript.interpreter.adapter

import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.Binary
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

class AstToIr {
    fun transform(program: List<ASTNode>): List<StmtIR> = program.map { toStmt(it) }

    private fun toStmt(n: ASTNode): StmtIR =
        when (n) {
            is DeclarationNode -> {
                val name = n.name
                val declaredType = mapType(n.type)
                val init = toExpr(n.value)
                DeclIR(name, declaredType, init)
            }

            is AssignationNode -> {
                val valueExpr = toExpr(n.type)
                AssignIR(n.name, valueExpr)
            }

            is PrintStatementNode -> PrintIR(toExpr(n.expression))

            else -> error("Nodo de sentencia no soportado: ${n::class.simpleName}")
        }

    private fun toExpr(n: ASTNode): ExprIR =
        when (n) {
            is LiteralNode<*> ->
                when (n.type.lowercase()) {
                    // number: admite Number directo o String numérica
                    "number" -> NumLit(numberFromAny(n.value))
                    "string" -> StrLit(n.value?.toString() ?: "")
                    "identifier" ->
                        IdRef(
                            n.value?.toString() ?: error("Identifier inválido (null) en LiteralNode"),
                        )

                    else -> error("Tipo de literal no soportado: '${n.type}' (valor=${n.value})")
                }

            is DoubleExpressionNode -> {
                val op = opFrom(n.operator)
                Binary(op, toExpr(n.left), toExpr(n.right))
            }

            else -> error("Nodo de expresión no soportado: ${n::class.simpleName}")
        }

    private fun mapType(s: String): RType =
        when (s.lowercase()) {
            "number" -> RType.NUMBER
            "string" -> RType.STRING
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

    private fun numberFromAny(v: Any?): Double =
        when (v) {
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull() ?: error("Literal number inválido: '$v'")
            else -> error("Literal number inválido: $v")
        }
}
