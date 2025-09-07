package org.printscript.linter

import org.printscript.linter.issue.Issue
import org.printscript.linter.rules.LintContext
import org.printscript.linter.rules.Rule
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode

class Linter(
    private val rules: List<Rule>,
    private val config: LintConfig = LintConfig(),
) {
    fun analyze(program: List<ASTNode>): List<Issue> {
        val ctx = LintContext(config)
        val out = mutableListOf<Issue>()

        fun visit(node: ASTNode) {
            // 1) aplica todas las reglas al nodo actual en el q estamos
            for (rule in rules) out += rule.check(node, ctx)

            // 2) descender a hijos segun el tipo
            when (node) {
                is DeclarationNode -> visit(node.value)
                // 'type' aca es el valor asignado (expr)
                is AssignationNode -> visit(node.type)
                is DoubleExpressionNode -> {
                    visit(node.left)
                    visit(node.right)
                }
                is PrintNode -> visit(node.expression)

                // sin hijos
                is LiteralNode<*> -> { }
                // no-op
            }
        }

        for (n in program) visit(n)
        return out
    }
}
