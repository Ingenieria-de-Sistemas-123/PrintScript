package org.printscript.cli.commands

import org.printscript.cli.adapters.CliFailure
import org.printscript.cli.adapters.FrontendAdapter
import org.printscript.cli.adapters.InterpreterAdapter
import org.printscript.cli.common.ErrorPrinter
import org.printscript.interpreter.io.FileInputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.StdinInputProvider
import org.printscript.interpreter.io.SystemEnvProvider
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable

@Command(name = "execute", description = ["Execute a PrintScript program."], mixinStandardHelpOptions = true)
class ExecuteCmd : Callable<Int> {
    @Option(names = ["-f", "--file"], required = true)
    lateinit var file: String

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Show this help message and exit"],
    )
    var helpRequested: Boolean = false

    @Option(
        names = ["--version", "--ps-version"],
        description = ["Language version to use (e.g. 1.0 or 1.1)"],
        defaultValue = "1.0",
    )
    var languageVersion: String = "1.0"

    @Option(
        names = ["--inputs-file"],
        description = ["Path to a text file with one input per line (used by readInput)"],
    )
    var inputsFile: String? = null

    override fun call(): Int {
        val fe = FrontendAdapter(languageVersion)
        val source = File(file).readText()

        val inputProvider = inputsFile?.let { FileInputProvider(File(it).toPath()) } ?: StdinInputProvider()
        val envProvider = SystemEnvProvider()
        val io = IOContext(inputProvider, envProvider)

        val res = fe.parseProgram(source, file)

        return res.fold(
            onSuccess = { ast ->
                InterpreterAdapter().run(ast, languageVersion, io)
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
