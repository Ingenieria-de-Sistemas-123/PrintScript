package org.printscript.parser.builder

import org.printscript.parser.node.ASTNode

interface Builder {
    fun build(): ASTNode
}