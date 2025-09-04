package org.printscript.rules

import org.printscript.LintConfig

class LintContext(val config: LintConfig) {
    val symbols: MutableMap<String, String> = linkedMapOf()
}