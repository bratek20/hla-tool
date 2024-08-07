// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.utils.directory.fixtures

import com.github.bratek20.utils.directory.api.*

fun diffFileName(given: FileName, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffDirectoryName(given: DirectoryName, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffPath(given: Path, expected: String, path: String = ""): String {
    if (pathGetValue(given) != expected) { return "${path}value ${pathGetValue(given)} != ${expected}" }
    return ""
}

fun diffFileContent(given: FileContent, expected: String, path: String = ""): String {
    if (fileContentGetValue(given) != expected) { return "${path}value ${fileContentGetValue(given)} != ${expected}" }
    return ""
}

data class ExpectedFile(
    var name: String? = null,
    var content: String? = null,
)
fun diffFile(given: File, expectedInit: ExpectedFile.() -> Unit, path: String = ""): String {
    val expected = ExpectedFile().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffFileName(given.getName(), it) != "") { result.add(diffFileName(given.getName(), it, "${path}name.")) }
    }

    expected.content?.let {
        if (diffFileContent(given.getContent(), it) != "") { result.add(diffFileContent(given.getContent(), it, "${path}content.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedDirectory(
    var name: String? = null,
    var files: List<(ExpectedFile.() -> Unit)>? = null,
    var directories: List<(ExpectedDirectory.() -> Unit)>? = null,
)
fun diffDirectory(given: Directory, expectedInit: ExpectedDirectory.() -> Unit, path: String = ""): String {
    val expected = ExpectedDirectory().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffDirectoryName(given.getName(), it) != "") { result.add(diffDirectoryName(given.getName(), it, "${path}name.")) }
    }

    expected.files?.let {
        if (given.getFiles().size != it.size) { result.add("${path}files size ${given.getFiles().size} != ${it.size}"); return@let }
        given.getFiles().forEachIndexed { idx, entry -> if (diffFile(entry, it[idx]) != "") { result.add(diffFile(entry, it[idx], "${path}files[${idx}].")) } }
    }

    expected.directories?.let {
        if (given.getDirectories().size != it.size) { result.add("${path}directories size ${given.getDirectories().size} != ${it.size}"); return@let }
        given.getDirectories().forEachIndexed { idx, entry -> if (diffDirectory(entry, it[idx]) != "") { result.add(diffDirectory(entry, it[idx], "${path}directories[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedCompareResult(
    var same: Boolean? = null,
    var differences: List<String>? = null,
)
fun diffCompareResult(given: CompareResult, expectedInit: ExpectedCompareResult.() -> Unit, path: String = ""): String {
    val expected = ExpectedCompareResult().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.same?.let {
        if (given.getSame() != it) { result.add("${path}same ${given.getSame()} != ${it}") }
    }

    expected.differences?.let {
        if (given.getDifferences().size != it.size) { result.add("${path}differences size ${given.getDifferences().size} != ${it.size}"); return@let }
        given.getDifferences().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}differences[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}