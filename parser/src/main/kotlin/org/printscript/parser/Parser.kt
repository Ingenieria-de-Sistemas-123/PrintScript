package org.printscript.parser

import org.printscript.parser.node.ASTNode
import org.printscript.token.Token

interface Parser {
    fun parse(list: List<Token>): List<ASTNode>
}
