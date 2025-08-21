package org.printscript.parser

import nodes.ASTNode
import org.printscript.token.Token

interface Parser{
    fun parse(list: List<Token>): List<ASTNode>
}