package org.printscript.cli.commands

import org.printscript.cli.adapters.CliFailure
import org.printscript.cli.adapters.FrontendAdapter
import org.printscript.cli.adapters.LinterAdapter
import org.printscript.cli.common.ErrorPrinter
import org.printscript.cli.common.LanguageError
import org.printscript.cli.common.Span
import org.printscript.common.Position
import org.printscript.linter.issue.Issue
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable

@Command(name = "analyze", description = ["Run static analysis (linter)."], mixinStandardHelpOptions = true)
class AnalyzeCmd : Callable<Int> {
    @Option(names = ["-f", "--file"], required = true)
    lateinit var file: String

    @Option(names = ["--version"], defaultValue = "1.0")
    var languageVersion: String = "1.0"

    override fun call(): Int {
        val fe = FrontendAdapter(languageVersion)
        val lint = LinterAdapter()

        val source = File(file).readText()
        val res = fe.parseProgram(source, file)

        return res.fold(
            onSuccess = { ast ->
                val issues = lint.analyze(ast)
                if (issues.isEmpty()) {
                    println("No issues")
                    0
                } else {
                    println("Issues found (${issues.size}):")
                    ErrorPrinter.printAll(issues.map { it.toLanguageError(file) })
                    1
                }
            },
            onFailure = { t ->
                val err = (t as? CliFailure)?.error
                if (err != null) ErrorPrinter.print(err) else System.err.println(t.message)
                1
            },
        )
    }
}

private fun Issue.toLanguageError(sourcePath: String): LanguageError {
    val messageText = "[${this.severity}] ${this.message}"
    val pos = Position(1, 1)
    return LanguageError(message = messageText, sourcePath = sourcePath, span = Span(pos, pos))
}
