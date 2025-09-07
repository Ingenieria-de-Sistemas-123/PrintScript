package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.rules.Rule
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.linter.testutil.AstFactory

class LinterRecursiveTraversalTest {
    @Test
    fun detecta_concat_mixta_dentro_de_asignacion_anidada() {
        val rules: List<Rule> = listOf(StringNumberConcatRule())
        val linter = Linter(rules, LintConfig())

        // y = (1 + ("a" + 2))  -> hay concat mixta "a" + 2 dentro
        val inner = AstFactory.bin(AstFactory.litString("\"a\""), "+", AstFactory.litNumber("2"), 2, 3)
        val outer = AstFactory.bin(AstFactory.litNumber("1"), "+", inner, 2, 1)
        val program = listOf(AstFactory.assign("y", outer, 1, 1))

        val issues = linter.analyze(program)
        assertTrue(issues.any { it.ruleId == "string-number-concat" })
    }
}
