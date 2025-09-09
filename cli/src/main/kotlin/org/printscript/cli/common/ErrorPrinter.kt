package org.printscript.cli.common

object ErrorPrinter {
    fun print(error: LanguageError) {
        System.err.println(error.toString())
    }

    fun printAll(errors: Iterable<LanguageError>) {
        for (e in errors) print(e)
    }
}
