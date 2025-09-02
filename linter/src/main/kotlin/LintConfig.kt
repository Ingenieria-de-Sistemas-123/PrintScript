package org.printscript

enum class IdentifierStyle { CAMEL_CASE, SNAKE_CASE }

data class LintConfig(
    val identifierStyle: IdentifierStyle = IdentifierStyle.CAMEL_CASE
)