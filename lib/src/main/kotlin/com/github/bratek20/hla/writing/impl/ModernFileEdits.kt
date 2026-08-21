package com.github.bratek20.hla.writing.impl

/**
 * Pure line edits for the shared files a modern TypeScript profile maintains.
 *
 * Kept separate from FilesModifiers so they can be tested without touching disk, and
 * separate from the legacy edits so that the legacy output cannot drift.
 */

private const val LAUNCH_CONFIG_NAME_PREFIX = "Debug Modern Tests - "

fun addEntryImport(lines: List<String>, importLine: String): List<String> {
    if (lines.any { it.trim() == importLine }) {
        return lines
    }

    val isImport = { line: String -> line.trimStart().startsWith("import ") }
    val header = lines.takeWhile { !isImport(it) }.dropLastWhile { it.isBlank() }
    val imports = (lines.filter { isImport(it) }.map { it.trim() } + importLine).distinct().sorted()

    return if (header.isEmpty()) imports else header + "" + imports
}

fun addModernTestScript(lines: List<String>, moduleName: String, vitestConfigPath: String): List<String> {
    val key = "\"test $moduleName\""
    if (lines.any { it.contains(key) }) {
        return lines
    }

    val command = "npm run typecheck_modern_tests && vitest run --config $vitestConfigPath $moduleName"
    return insertIntoBlock(lines, "\"scripts\"", '{', '}') { padding ->
        listOf("$padding$key: \"$command\"")
    }
}

fun addModernLaunchConfig(lines: List<String>, moduleName: String, vitestConfigPath: String): List<String> {
    val name = "$LAUNCH_CONFIG_NAME_PREFIX$moduleName"
    if (lines.any { it.contains("\"$name\"") }) {
        return lines
    }

    return insertIntoBlock(lines, "\"configurations\"", '[', ']') { padding ->
        listOf(
            "$padding{",
            "$padding    \"type\": \"node\",",
            "$padding    \"request\": \"launch\",",
            "$padding    \"name\": \"$name\",",
            "$padding    \"program\": \"\${workspaceFolder}/node_modules/vitest/vitest.mjs\",",
            "$padding    \"args\": [",
            "$padding        \"run\",",
            "$padding        \"--config\",",
            "$padding        \"$vitestConfigPath\",",
            "$padding        \"--no-file-parallelism\",",
            "$padding        \"--testTimeout=600000\",",
            "$padding        \"$moduleName\"",
            "$padding    ],",
            "$padding    \"cwd\": \"\${workspaceFolder}\",",
            "$padding    \"console\": \"integratedTerminal\",",
            "$padding    \"autoAttachChildProcesses\": true,",
            "$padding    \"smartStep\": true,",
            "$padding    \"skipFiles\": [",
            "$padding        \"<node_internals>/**\",",
            "$padding        \"**/node_modules/**\"",
            "$padding    ]",
            "$padding}",
        )
    }
}

// Inserts as the last element of the block opened on the line containing `anchor`,
// adding the separating comma to the previous element when it does not have one.
private fun insertIntoBlock(
    lines: List<String>,
    anchor: String,
    open: Char,
    close: Char,
    build: (padding: String) -> List<String>
): List<String> {
    val openIndex = lines.indexOfFirst { it.contains(anchor) }
    if (openIndex == -1) {
        return lines
    }
    val closeIndex = matchingCloseIndex(lines, openIndex, open, close)
    if (closeIndex == -1) {
        return lines
    }

    val result = lines.toMutableList()
    val lastElementIndex = closeIndex - 1
    val hasElements = lastElementIndex > openIndex

    // With existing elements the file tells us its indentation. With an empty block we
    // step in by the opening key's own indent, which is already exactly one level.
    val openIndentation = indentationOf(result[openIndex])
    val padding = when {
        hasElements -> indentationOf(result[lastElementIndex])
        openIndentation.isNotEmpty() -> openIndentation + openIndentation
        else -> "    "
    }

    if (hasElements && !result[lastElementIndex].trimEnd().endsWith(",")) {
        result[lastElementIndex] = result[lastElementIndex].trimEnd() + ","
    }

    result.addAll(closeIndex, build(padding))
    return result
}

private fun indentationOf(line: String): String {
    return line.takeWhile { it == ' ' }
}

private fun matchingCloseIndex(lines: List<String>, openIndex: Int, open: Char, close: Char): Int {
    var depth = 0
    var started = false

    for (index in openIndex until lines.size) {
        for (char in outsideStrings(lines[index])) {
            if (char == open) {
                depth++
                started = true
            } else if (char == close) {
                depth--
            }
        }
        if (started && depth == 0) {
            return index
        }
    }
    return -1
}

// JSON values such as "${workspaceFolder}" carry braces that must not be counted
private fun outsideStrings(line: String): String {
    val result = StringBuilder()
    var inString = false
    var index = 0

    while (index < line.length) {
        val char = line[index]
        when {
            char == '\\' && inString -> index++
            char == '"' -> inString = !inString
            !inString -> result.append(char)
        }
        index++
    }
    return result.toString()
}
