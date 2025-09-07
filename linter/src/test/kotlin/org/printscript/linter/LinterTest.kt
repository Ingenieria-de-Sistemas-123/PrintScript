package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.rules.NoDuplicateVariableRule
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.linter.rules.Rule
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.linter.testutil.AstFactory

class LinterTest {
    @Test
    fun orquesta_reglas_y_reutiliza_contexto() {
        val rules: List<Rule> =
            listOf(
                NoDuplicateVariableRule(),
                PrintlnRestrictionRule(),
                StringNumberConcatRule(),
            )
        val linter = Linter(rules, LintConfig())

        val program =
            listOf(
                // declara x
                AstFactory.decl("x", "number", AstFactory.litNumber("0"), 1, 1),
                // println( "ok" ) -> permitido
                AstFactory.print(AstFactory.litString("\"ok\""), 2, 1),
                // x = 1 + "s" -> concat mixta -> warning
                AstFactory.assign("x", AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litString("\"s\""), 3, 5), 3, 1),
                // redeclaracion x -> error
                AstFactory.decl("x", "number", AstFactory.litNumber("1"), 4, 2),
                // println(1) -> no permitido
                AstFactory.print(AstFactory.litNumber("1"), 5, 1),
            )

        val issues = linter.analyze(program)
        // Esperamos: 1 warning por concat mixta, 1 error por duplicado, 1 warning por println inválido = 3
        assertEquals(3, issues.size)
        assertTrue(issues.any { it.ruleId == "string-number-concat" })
        assertTrue(issues.any { it.ruleId == "no-duplicate-var" && it.severity.name == "ERROR" })
        assertTrue(issues.any { it.ruleId == "println-restriction" })
    }
}
