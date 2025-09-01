package org.printscript

import DefaultParser
import org.printscript.lexer.Lexer
import org.printscript.rules.NoDuplicateVariableRule
import org.printscript.rules.PrintlnRestrictionRule
import org.printscript.rules.StringNumberConcatRule

fun main() {
    val rules = listOf(NoDuplicateVariableRule(), PrintlnRestrictionRule(), StringNumberConcatRule())
    val config = LintConfig(
        IdentifierStyle.SNAKE_CASE
    )
    val linter = Linter(rules, config)
    val code = "let i: number = 0; println(i + i);"
    val lexer = Lexer()
    val parser = DefaultParser()
    val tokens = lexer.lex(code)
    val ast = parser.parse(tokens)
    val issues = linter.analyze(ast)
    issues.forEach {
        println("Issue: ${it.ruleId} at ${it.severity}: ${it.message}")
    }
}
