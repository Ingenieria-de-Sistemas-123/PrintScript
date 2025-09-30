package org.printscript.linter

enum class IdentifierStyle { CAMEL_CASE, SNAKE_CASE }

data class LintConfig(
    val identifierStyle: IdentifierStyle? = null,
)
