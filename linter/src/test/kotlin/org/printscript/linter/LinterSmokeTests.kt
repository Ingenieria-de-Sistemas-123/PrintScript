package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.rules.NoDuplicateVariableRule
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.linter.testutil.TestUtils.declVar
import org.printscript.linter.testutil.TestUtils.num
import org.printscript.linter.testutil.TestUtils.printlnNode
import org.printscript.linter.testutil.TestUtils.str

class LinterSmokeTests {
    @Test
    fun `println with literal is allowed (no println-restriction issue)`() {
        val linter =
            Linter(
                rules =
                    listOf(
                        PrintlnRestrictionRule(),
                    ),
            )

        val program =
            listOf(
                printlnNode(str("ok"), 1, 1),
            )

        val issues = linter.analyze(program)
        assertFalse(
            issues.any { it.ruleId.equals("println-restriction", ignoreCase = true) },
            "No debería marcar println-restriction para un literal; issues=${issues.map { it.ruleId }}",
        )
    }

    @Test
    fun `duplicate variable is reported robustly`() {
        val linter =
            Linter(
                rules =
                    listOf(
                        NoDuplicateVariableRule(),
                    ),
            )

        val program =
            listOf(
                declVar("x", "number", num(1), 1, 1),
                declVar("x", "number", num(2), 2, 1),
            )

        val issues = linter.analyze(program)
        val hasDuplicate =
            issues.any { it.ruleId.contains("duplicate", ignoreCase = true) }

        assertTrue(
            hasDuplicate,
            "Se esperaba un issue de variable duplicada; ruleIds=${issues.map { it.ruleId }}",
        )
    }
}
