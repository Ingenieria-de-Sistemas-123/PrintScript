package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.rules.NoDuplicateVariableRule
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.linter.testutil.TestUtils.assign
import org.printscript.linter.testutil.TestUtils.declVar
import org.printscript.linter.testutil.TestUtils.ifElse
import org.printscript.linter.testutil.TestUtils.num
import org.printscript.linter.testutil.TestUtils.plus
import org.printscript.linter.testutil.TestUtils.printlnNode
import org.printscript.linter.testutil.TestUtils.str

class LinterTraversalTest {
    @Test
    fun `linter traverses declaration, assignation, expressions, println and if-else`() {
        val linter =
            Linter(
                rules =
                    listOf(
                        NoDuplicateVariableRule(),
                        PrintlnRestrictionRule(),
                        StringNumberConcatRule(),
                    ),
            )

        val program =
            listOf(
                declVar("x", "number", num(1), 1, 1),
                assign("x", plus(num(2), num(3)), 2, 1),
                printlnNode(str("hi"), 3, 1),
                ifElse(
                    ifNodes = listOf(printlnNode(plus(str("a"), num(1)))),
                    elseNodes = emptyList(),
                    condition = num(1),
                ),
            )

        val issues = linter.analyze(program)

        assertTrue(issues.any { it.ruleId == "string-number-concat" })
        assertTrue(issues.any { it.ruleId == "println-restriction" })
    }
}
