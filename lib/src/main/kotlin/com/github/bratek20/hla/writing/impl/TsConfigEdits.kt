package com.github.bratek20.hla.writing.impl

/**
 * Pure line edits for the tsconfig "files"/"include" list.
 *
 * A module owns one block of the list, delimited by `//<module> start` and `//<module> end`
 * comments and split into a group per submodule. Paths already present anywhere in the list
 * are moved into that block, so starting a module twice changes nothing.
 */

fun addModuleFilesToTsConfig(
    lines: List<String>,
    moduleDirectory: String,
    submodulesPaths: List<List<String>>
): List<String> {
    if (submodulesPaths.isEmpty()) {
        return lines
    }

    val listStartIndex = lines.indexOfFirst { it.contains("\"files\"") || it.contains("\"include\"") }
    if (listStartIndex == -1) {
        return lines
    }
    val listEndOffset = lines.subList(listStartIndex, lines.size).indexOfFirst { it.contains("]") }
    if (listEndOffset == -1) {
        return lines
    }

    val result = lines.toMutableList()
    var indexToAdd = listStartIndex + listEndOffset
    val padding = indentationOf(result[indexToAdd]) + "    "
    val startComment = "$padding//$moduleDirectory start"
    val endComment = "$padding//$moduleDirectory end"

    val newLines = mutableListOf<String>()
    submodulesPaths.forEachIndexed { index, paths ->
        newLines.add("")
        if (index == 0) {
            newLines.add(startComment)
        }
        paths.forEach { path ->
            newLines.add("$padding\"$path\",")
            if (result.removeIf { line -> line.contains("\"$path") }) {
                indexToAdd--
            }
        }
    }
    newLines.add(endComment)

    result.addAll(indexToAdd, newLines)

    removeDuplicatedComments(result, startComment, endComment)
    removeRedundantBlankLines(result, moduleDirectory)

    return result
}

// Moving already registered paths leaves the previous block's comments behind.
private fun removeDuplicatedComments(lines: MutableList<String>, startComment: String, endComment: String) {
    val firstStartIndex = lines.indexOfFirst { it.contains(startComment) }
    val lastEndIndex = lines.indexOfLast { it.contains(endComment) }

    removeLines(lines) { index, line ->
        line == startComment && index != firstStartIndex || line == endComment && index != lastEndIndex
    }
}

private fun removeRedundantBlankLines(lines: MutableList<String>, moduleDirectory: String) {
    removeLines(lines) { index, line ->
        val previous = lines.getOrNull(index - 1)
        line.isBlank() && (previous?.isBlank() == true || previous?.contains("//$moduleDirectory start") == true)
    }
}

private fun removeLines(lines: MutableList<String>, predicate: (Int, String) -> Boolean) {
    lines.mapIndexedNotNull { index, line -> index.takeIf { predicate(index, line) } }
        .reversed()
        .forEach { lines.removeAt(it) }
}
