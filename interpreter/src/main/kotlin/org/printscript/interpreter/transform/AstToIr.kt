package org.printscript.interpreter.transform

import node.ASTNode
import node.AssignationNode
import node.DeclarationNode
import node.DoubleExpressionNode
import node.LiteralNode
import node.PrintNode
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.ExprIR
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.Binary
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.Op
import org.printscript.interpreter.ir.StmtIR
import org.printscript.interpreter.runtime.RType

/**
 * usamos este adapter para transformar node.ASTNode (parser) → IR del intérprete.
 * Es lo ÚNICO que conoce a 'node'. Si el parser cambia, tocamos SOLO esto.
 */
class AstToIr {

    fun transform(program: List<ASTNode>): List<StmtIR> =
        program.map { toStmt(it) }

    // sentencias
    private fun toStmt(n: ASTNode): StmtIR = when (n) {
        is DeclarationNode -> {
            val name = n.name
            val declaredType = mapType(n.type)       // "number"/"string"
            val init = toExpr(n.value)               // ASTNode --> ExprIR
            DeclIR(name, declaredType, init)
        }
        is AssignationNode -> {
            AssignIR(n.name, toExpr(n.type))
        }
        is PrintNode -> PrintIR(toExpr(n.expression))
        else -> error("Nodo de sentencia no soportado: ${n::class.simpleName}")
    }

    // expressions
    private fun toExpr(n: ASTNode): ExprIR = when (n) {
        is LiteralNode<*> -> when (n.type.lowercase()) {
            "number"     -> NumLit(numberFromAny(n.value))
            "string"     -> StrLit(n.value?.toString() ?: "")
            "identifier" -> IdRef(n.value?.toString()
                ?: error("Identifier inválido (null)"))
            else -> error("Tipo de literal no soportado: '${n.type}'")
        }
        is DoubleExpressionNode -> Binary(opFrom(n.operator), toExpr(n.left), toExpr(n.right))
        else -> error("Nodo de expresión no soportado: ${n::class.simpleName}")
    }

    // Helpers
    private fun mapType(s: String): RType = when (s.lowercase()) {
        "number" -> RType.NUMBER
        "string" -> RType.STRING
        else -> error("Tipo de declaración desconocido '$s'")
    }

    private fun opFrom(op: String): Op = when (op) {
        "+" -> Op.PLUS
        "-" -> Op.MINUS
        "*" -> Op.STAR
        "/" -> Op.SLASH
        else -> error("Operador desconocido '$op'")
    }

    private fun numberFromAny(v: Any?): Double = when (v) {
        is Number -> v.toDouble()
        is String -> v.toDoubleOrNull() ?: error("Literal number inválido: '$v'")
        else -> error("Literal number inválido: $v")
    }
}
