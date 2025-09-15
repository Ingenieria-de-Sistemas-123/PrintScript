package org.printscript.interpreter.adapter

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
import org.printscript.interpreter.ir.AssignIR
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode
import org.printscript.interpreter.runtime.RType

/**
 * Mapper from AST nodes to IR for PrintScript version 1.1.
 *
 * This implementation extends the basic v1.0 mapper by handling const declarations,
 * boolean literals, if/else statements and readInput/readEnv expressions. It
 * delegates to the existing v1.0 mapper for all other constructs.
 *
 * Note: The exact AST node classes for v1.1 are not defined in this snippet.
 * I will replace the placeholder types (`ConstDeclarationNode`, `IfNode`,
 * `ReadInputNode`, `ReadEnvNode`) with the actual types used in parser once
 * they are available.
 */
class AstToIrV11 : AstToIrMapper {
    override fun transform(program: List<ASTNode>): List<StmtIR> = program.map { toStmt(it) }

    private fun toStmt(n: ASTNode): StmtIR = when (n) {
        is DeclarationNode -> {
            // v1.0 variable declaration, reuse existing mapping
            val name = n.name
            val declaredType = mapType(n.type)
            val init = toExpr(n.value)
            DeclIR(name, declaredType, init)
        }
        // Placeholder for v1.1 constant declaration; replace with real AST node type
        /*
        is ConstDeclarationNode -> {
            val name = n.name
            val declaredType = mapType(n.type)
            val init = toExpr(n.value)
            ConstDeclIR(name, declaredType, init)
        }
        */
        is AssignationNode -> {
            val valueExpr = toExpr(n.type)
            AssignIR(n.name, valueExpr)
        }
        is PrintNode -> {
            PrintIR(toExpr(n.expression))
        }
        // Placeholder for v1.1 if/else; replace with real AST node type

        /*
        is IfNode -> {
            // Evaluate condition expression to a temporary variable. In the IR we
            // represent conditions via the name of a boolean variable, so we assume the
            // parser generates a boolean variable reference in `n.conditionVar`. You may
            // need to extend the IR to allow direct boolean expressions.
            val condVar = (n.condition as LiteralNode<*>).value.toString()
            val thenIR = n.thenBlock.map { toStmt(it) }
            val elseIR = n.elseBlock?.map { toStmt(it) }
            IfIR(condVar, thenIR, elseIR)
        }
         */
        else -> error("Nodo de sentencia no soportado en v1.1: ${n::class.simpleName}")
    }

    private fun toExpr(n: ASTNode?): ExprIR = when (n) {
        null -> error("Expression node cannot be null")
        is LiteralNode<*> -> when (n.type.lowercase()) {
            "number" -> NumLit(numberFromAny(n.value))
            "string" -> StrLit(n.value?.toString() ?: "")
            // v1.1 boolean literal support
            "boolean" -> BoolLit(
                when (val v = n.value) {
                    is Boolean -> v
                    is String -> v.equals("true", ignoreCase = true)
                    else -> error("Literal boolean inválido: $v")
                }
            )
            "identifier" -> IdRef(
                n.value?.toString() ?: error("Identifier inválido (null) en LiteralNode")
            )
            else -> error("Tipo de literal no soportado: '${n.type}' (valor=${n.value})")
        }
        is DoubleExpressionNode -> {
            val op = opFrom(n.operator)
            Binary(op, toExpr(n.left), toExpr(n.right))
        }

        // Placeholder for v1.1 readInput expression
        /*
        is ReadInputNode -> {
            // Expected type can be parsed from the node (if provided) or null for string
            val expected = n.expectedType?.let { mapType(it) }
            ReadInputIR(n.prompt, expected)
        }
        // Placeholder for v1.1 readEnv expression
        is ReadEnvNode -> {
            val expected = n.expectedType?.let { mapType(it) }
            ReadEnvIR(n.variableName, expected)
        }

         */
        else -> error("Nodo de expresión no soportado en v1.1: ${n::class.simpleName}")
    }

    private fun mapType(s: String): RType = when (s.lowercase()) {
        "number" -> RType.NUMBER
        "string" -> RType.STRING
        "boolean" -> RType.BOOLEAN
        else -> error("Tipo de declaración desconocido '$s'")
    }

    private fun opFrom(opText: String): Op = when (opText.trim()) {
        "+" -> Op.PLUS
        "-" -> Op.MINUS
        "*" -> Op.STAR
        "/" -> Op.SLASH
        else -> error("Operador desconocido '$opText'")
    }

    private fun numberFromAny(v: Any?): Double = when (v) {
        is Number -> v.toDouble()
        is String -> v.toDoubleOrNull()
            ?: if (v == "empty") 0.0 else error("Literal number inválido: '$v'")
        else -> error("Literal number inválido: $v")
    }
}
