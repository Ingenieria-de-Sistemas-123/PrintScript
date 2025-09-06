package org.printscript.linter.rules

import org.printscript.linter.LintConfig

class LintContext(val config: LintConfig) {
    val symbols: MutableMap<String, String> = linkedMapOf()
}
