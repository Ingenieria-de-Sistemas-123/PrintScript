package org.printscript.cli.commands

import org.printscript.cli.adapters.CliFailure
import org.printscript.cli.adapters.FormatterAdapter
import org.printscript.cli.adapters.FrontendAdapter
import org.printscript.cli.common.ErrorPrinter
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable

@Command(name = "format", description = ["Format a PrintScript source file."], mixinStandardHelpOptions = true)
class FormatCmd : Callable<Int> {
    @Option(names = ["-f", "--file"], required = true)
    lateinit var file: String

    @Option(names = ["-c", "--config"])
    var configPath: String? = null

    @Option(names = ["--check"], defaultValue = "false")
    var check: Boolean = false

    @Option(names = ["--apply"], defaultValue = "false")
    var apply: Boolean = false

    @Option(names = ["--version"], defaultValue = "1.0")
    var languageVersion: String = "1.0"

    override fun call(): Int {
        if (check && apply) {
            System.err.println("Use only one: --check OR --apply.")
            return 2
        }

        val fe = FrontendAdapter(languageVersion)
        val fmt = FormatterAdapter()

        val sourceFile = File(file)
        val source = sourceFile.readText()
        val res = fe.parseProgram(source, file)

        return res.fold(
            onSuccess = { ast ->
                val cfg = fmt.loadConfig(configPath)
                val formatted = fmt.format(ast, cfg, source)

                if (check) {
                    return if (formatted == source) {
                        println("OK: $file is already formatted.")
                        0
                    } else {
                        println("File is not formatted. Suggested output:\n")
                        println(formatted)
                        1
                    }
                } else {
                    if (apply) {
                        if (formatted != source) {
                            sourceFile.writeText(formatted)
                            println("Formatting applied to $file")
                        } else {
                            println("OK: $file is already formatted.")
                        }
                    } else {
                        println(formatted)
                    }
                    return 0
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
