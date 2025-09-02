package org.printscript.issue

data class Issue(
    val ruleId: String,
    val message: String,
    val startLine: Int,
    val startCol: Int,
    val endLine: Int,
    val endCol: Int,
    val severity: Severity = Severity.WARNING
)