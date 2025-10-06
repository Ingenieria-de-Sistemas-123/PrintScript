package org.printscript.formatter.rules

internal fun StringBuilder.trimTrailingSpaces() {
    while (isNotEmpty() && this[length - 1] == ' ') {
        deleteCharAt(length - 1)
    }
}
