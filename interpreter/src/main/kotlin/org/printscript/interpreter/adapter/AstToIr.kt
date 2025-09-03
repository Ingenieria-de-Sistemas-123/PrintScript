package org.printscript.interpreter.adapter

import node.ASTNode
import node.AssignationNode
import node.DeclarationNode
import node.DoubleExpressionNode
import node.LiteralNode
import node.PrintNode
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

/**
 * Adapter: PARSER (node.*) -> IR del intérprete (org.printscript.interpreter.ir.*)
 *
 * Es la ÚNICA pieza que conoce al AST del parser.
 * Si mañana cambian los nodos del parser, tocamos SOLO este archivo.
 *
 * Invariantes y diseño:
 * - NO retenemos referencias al AST: devolvemos IR inmutable y listo (evita retención de memoria).
 * - Endurecemos casos límites (operator/numero inválido) con errores claros.
 * - Mapeamos `LiteralNode(type="identifier")` -> IdRef(name).
 * - En AssignationNode, el campo `type` del parser es en realidad el VALOR a asignar
 *   (naming raro del parser; lo documentamos y lo manejamos).
 */
class AstToIr {

    /** Programa = lista de sentencias del parser -> lista de sentencias IR */
    fun transform(program: List<ASTNode>): List<StmtIR> =
        program.map { toStmt(it) }

    /** Sentencia del parser -> StmtIR */
    private fun toStmt(n: ASTNode): StmtIR = when (n) {
        is DeclarationNode -> {
            val name = n.name
            val declaredType = mapType(n.type)          // "number"/"string" -> RType
            val init = toExpr(n.value)                  // AST expr -> ExprIR
            DeclIR(name, declaredType, init)
        }

        is AssignationNode -> {
            // OJO: en el parser, el campo se llama "type" pero semánticamente es el VALOR a asignar.
            val valueExpr = toExpr(n.type)
            AssignIR(n.name, valueExpr)
        }

        is PrintNode -> PrintIR(toExpr(n.expression))

        else -> error("Nodo de sentencia no soportado: ${n::class.simpleName}")
    }

    /** Expresión del parser -> ExprIR */
    private fun toExpr(n: ASTNode): ExprIR = when (n) {

        is LiteralNode<*> -> when (n.type.lowercase()) {
            // number: admite Number directo o String numérica
            "number"     -> NumLit(numberFromAny(n.value))
            "string"     -> StrLit(n.value?.toString() ?: "")
            "identifier" -> IdRef(
                n.value?.toString() ?: error("Identifier inválido (null) en LiteralNode")
            )
            // Si en el futuro agregan boolean en el parser y en tu IR:
            // "boolean"    -> BoolLit((n.value as? Boolean) ?: error("Boolean inválido"))
            else -> error("Tipo de literal no soportado: '${n.type}' (valor=${n.value})")
        }

        is DoubleExpressionNode -> {
            val op = opFrom(n.operator)
            Binary(op, toExpr(n.left), toExpr(n.right))
        }

        else -> error("Nodo de expresión no soportado: ${n::class.simpleName}")
    }

    //helpers de mapeo

    private fun mapType(s: String): RType = when (s.lowercase()) {
        "number" -> RType.NUMBER
        "string" -> RType.STRING
        else     -> error("Tipo de declaración desconocido '$s'")
    }

    private fun opFrom(opText: String): Op = when (opText.trim()) {
        "+"  -> Op.PLUS
        "-"  -> Op.MINUS
        "*"  -> Op.STAR
        "/"  -> Op.SLASH
        else -> error("Operador desconocido '$opText'")
    }

    private fun numberFromAny(v: Any?): Double = when (v) {
        is Number -> v.toDouble()
        is String -> v.toDoubleOrNull()
            ?: error("Literal number inválido: '$v'")
        else      -> error("Literal number inválido: $v")
    }
}
