package org.printscript.formatter.render

import org.printscript.formatter.rules.CodeFormatRule
import org.printscript.formatter.rules.KeywordSpacingRule
import org.printscript.formatter.rules.SemicolonLineBreakRule
import org.printscript.formatter.rules.SpaceAroundColon
import org.printscript.formatter.rules.SpaceAroundEquals
import org.printscript.formatter.rules.SpaceAroundOperator

object DefaultRules {
    fun standard(): List<CodeFormatRule> =
        listOf(
            SpaceAroundEquals(),
            SpaceAroundColon(),
            SpaceAroundOperator(),
            SemicolonLineBreakRule(),
            KeywordSpacingRule(),
        )
}
