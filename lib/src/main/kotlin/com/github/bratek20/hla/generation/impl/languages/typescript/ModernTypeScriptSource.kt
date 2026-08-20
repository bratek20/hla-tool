package com.github.bratek20.hla.generation.impl.languages.typescript

private val NAMESPACE_LINE = Regex("^(\\s*)namespace [\\w.]+ \\{\\s*$")

private val TOP_LEVEL_DECLARATION = Regex(
    "^(export\\s+)?(declare\\s+)?(abstract\\s+class|class|interface|enum|type|function|async\\s+function|const|let|var)\\s+([A-Za-z_$][\\w$]*)"
)

private const val INDENT = "    "

/**
 * Line-level surgery on already generated TypeScript, turning the namespace-based
 * output into ES modules. Works uniformly for velocity and code builder patterns
 * because every pattern file funnels through PatternGenerator.generatePatternFile.
 */
object ModernTypeScriptSource {
    fun unwrapNamespaces(lines: List<String>): List<String> {
        var current = lines
        while (true) {
            current = unwrapFirstNamespace(current) ?: return current
        }
    }

    fun addMissingExports(lines: List<String>): List<String> {
        return lines.map { line ->
            val match = TOP_LEVEL_DECLARATION.find(line)
            if (match != null && match.groupValues[1].isEmpty()) "export $line" else line
        }
    }

    fun declaredNames(lines: List<String>): Set<String> {
        return lines.mapNotNull { TOP_LEVEL_DECLARATION.find(it)?.groupValues?.get(4) }.toSet()
    }

    private fun unwrapFirstNamespace(lines: List<String>): List<String>? {
        val openIndex = lines.indexOfFirst { NAMESPACE_LINE.matches(it) }
        if (openIndex == -1) {
            return null
        }
        val closeIndex = findClosingBraceIndex(lines, openIndex)

        val body = lines.subList(openIndex + 1, closeIndex).map { dedent(it) }
        return lines.subList(0, openIndex) + body + lines.subList(closeIndex + 1, lines.size)
    }

    private fun findClosingBraceIndex(lines: List<String>, openIndex: Int): Int {
        var depth = 0
        for (index in openIndex until lines.size) {
            depth += braceBalance(lines[index])
            if (index > openIndex && depth == 0) {
                return index
            }
        }
        return lines.size - 1
    }

    private fun dedent(line: String): String {
        return if (line.startsWith(INDENT)) line.substring(INDENT.length) else line.trimStart()
    }

    // Counts braces outside of string literals and line comments, so that template
    // strings like `${path}items[${idx}].` and quoted braces cannot skew the balance.
    private fun braceBalance(line: String): Int {
        var balance = 0
        var index = 0
        while (index < line.length) {
            when (val char = line[index]) {
                '/' -> {
                    if (index + 1 < line.length && line[index + 1] == '/') {
                        return balance
                    }
                    index++
                }
                '"', '\'', '`' -> index = skipStringLiteral(line, index, char)
                '{' -> { balance++; index++ }
                '}' -> { balance--; index++ }
                else -> index++
            }
        }
        return balance
    }

    private fun skipStringLiteral(line: String, startIndex: Int, quote: Char): Int {
        var index = startIndex + 1
        while (index < line.length) {
            when (line[index]) {
                '\\' -> index += 2
                quote -> return index + 1
                else -> index++
            }
        }
        return index
    }
}
