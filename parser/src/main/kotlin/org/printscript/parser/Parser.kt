package org.printscript.parser

import org.printscript.parser.node.ASTNode
import org.printscript.token.Token

interface Parser {
    fun parse(tokens: Sequence<Token>): Sequence<ASTNode>

    fun parse(list: List<Token>): List<ASTNode> = parse(list.asSequence()).toList()
}
