package org.printscript.linter

import org.printscript.linter.issue.Issue
import org.printscript.linter.rules.LintContext
import org.printscript.linter.rules.Rule
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

class Linter(
    private val rules: List<Rule>,
    private val config: LintConfig = LintConfig(),
) {
    fun analyze(program: List<ASTNode>): List<Issue> {
        val lintContext = LintContext(config)
        val issues = mutableListOf<Issue>()

        fun applyRules(node: ASTNode) {
            for (rule in rules) issues += rule.check(node, lintContext)
        }

        fun visit(node: ASTNode) {
            applyRules(node)
            when (node) {
                is DeclarationNode -> visit(node.expression)
                is AssignationNode -> visit(node.expression)
                is DoubleExpressionNode -> {
                    visit(node.left)
                    visit(node.right)
                }
                is PrintStatementNode -> visit(node.expression)
                is IfElseNode -> {
                    node.ifBranch.forEach(::visit)
                    node.elseBranch.forEach(::visit)
                }
                is LiteralNode<*> -> { }
                else -> { }
            }
        }
        program.forEach(::visit)
        return issues
    }
}
