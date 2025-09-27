package org.printscript.parser.node

import org.printscript.token.TokenType

data class LiteralNode<T>(
    val value: T,
    val tokenType: TokenType? = null,
) : ASTNode
