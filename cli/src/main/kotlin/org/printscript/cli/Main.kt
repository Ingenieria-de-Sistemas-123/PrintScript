package org.printscript.cli

import org.printscript.cli.commands.AnalyzeCmd
import org.printscript.cli.commands.ExecuteCmd
import org.printscript.cli.commands.FormatCmd
import org.printscript.cli.commands.ValidateCmd
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "printscript",
    mixinStandardHelpOptions = true,
    version = ["PrintScript CLI 1.0"],
    description = ["CLI para validar, ejecutar, formatear y analizar programas PrintScript."],
    subcommands = [ValidateCmd::class, ExecuteCmd::class, FormatCmd::class, AnalyzeCmd::class],
)
class Root

fun main(args: Array<String>) {
    val exit = CommandLine(Root()).execute(*args)
    kotlin.system.exitProcess(exit)
}
