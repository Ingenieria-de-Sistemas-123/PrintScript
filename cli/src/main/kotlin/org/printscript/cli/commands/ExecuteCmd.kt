package org.printscript.cli.commands

import org.printscript.cli.adapters.CliFailure
import org.printscript.cli.adapters.FrontendAdapter
import org.printscript.cli.adapters.InterpreterAdapter
import org.printscript.cli.common.ErrorPrinter
import org.printscript.interpreter.io.FileInputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable

@Command(name = "execute", description = ["Execute a PrintScript program."])
class ExecuteCmd : Callable<Int> {
    @Option(names = ["-f", "--file"], required = true)
    lateinit var file: String

    @Option(names = ["--input-file"], description = ["Optional file to read inputs from instead of stdin"])
    var inputFile: String? = null

    @Option(names = ["--version"], defaultValue = "1.0")
    var languageVersion: String = "1.0"

    override fun call(): Int {
        val fe = FrontendAdapter(languageVersion)
        val source =
            try {
                File(file).readText()
            } catch (io: IOException) {
                System.err.println("Could not read file '$file': ${io.message}")
                return 1
            }
        val res = fe.parseProgram(source, file)

        return res.fold(
            onSuccess = { ast ->
                val ioContext =
                    inputFile?.let {
                        try {
                            IOContext(FileInputProvider(File(it).toPath()), IOContext.systemDefault().env)
                        } catch (io: IOException) {
                            System.err.println("Could not open input file '$it': ${io.message}")
                            return@fold 1
                        }
                    } ?: IOContext.systemDefault()

                // OutputProvider que flushea al instante para entrega interactiva
                val flushingOutput =
                    OutputProvider { text ->
                        kotlin.io.println(text)
                        System.out.flush()
                    }

                InterpreterAdapter().run(ast, ioContext = ioContext, output = flushingOutput)
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
