package org.printscript.cli.commands

import org.printscript.cli.adapters.CliFailure
import org.printscript.cli.adapters.FrontendAdapter
import org.printscript.cli.adapters.InterpreterAdapter
import org.printscript.cli.common.ErrorPrinter
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable

@Command(name = "execute", description = ["Execute a PrintScript program."])
class ExecuteCmd : Callable<Int> {
    @Option(names = ["-f", "--file"], required = true)
    lateinit var file: String

    @Option(names = ["--version"], defaultValue = "1.0")
    var languageVersion: String = "1.0"

    override fun call(): Int {
        val fe = FrontendAdapter(languageVersion)
        val source = File(file).readText()
        val res = fe.parseProgram(source, file)

        return res.fold(
            onSuccess = { ast ->
                InterpreterAdapter().run(ast)
                0
            },
            onFailure = { t ->
                val err = (t as? CliFailure)?.error
                if (err != null) ErrorPrinter.print(err) else System.err.println(t.message)
                1
            },
        )
    }
}
