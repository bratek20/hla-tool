package pl.bratek20.hla.directory.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.directory.api.CompareResult
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File

data class ExpectedDirectoryExt(
    var name: String? = null,
    var files: List<ExpectedFileExt.() -> Unit>? = null,
    var hasSingleFiles: (ExpectedFileExt.() -> Unit)? = null,
    var hasSingleDirectories: (ExpectedDirectoryExt.() -> Unit)? = null,
    var hasNoDirectories: String? = null,
)

fun assertDirectoryExt(given: Directory, expectedOv: ExpectedDirectoryExt.() -> Unit) {
    val expected = ExpectedDirectoryExt().apply(expectedOv)

    if (expected.name != null) {
        assertThat(given.name.value).isEqualTo(expected.name)
    }

    if (expected.files != null) {
        assertThat(given.files).hasSameSizeAs(expected.files)
        expected.files!!.forEachIndexed { index, expectedFile ->
            assertFileExt(given.files[index], expectedFile)
        }
    }

    if (expected.hasSingleFiles != null) {
        val singleFiles = given.files.filter { diffFile(it, expected.hasSingleFiles!!) == "" }
        assertThat(singleFiles.size).withFailMessage("File not found").isEqualTo(1)
    }

    if (expected.hasSingleDirectories != null) {
        val expectedDirectory = ExpectedDirectoryExt().apply(expected.hasSingleDirectories!!)
        val directory = given.directories.find { it.name.value == expectedDirectory.name }
        assertThat(directory).isNotNull
        assertDirectoryExt(directory!!, expected.hasSingleDirectories!!)
    }

    if (expected.hasNoDirectories != null) {
        assertThat(given.directories.find { it.name.value == expected.hasNoDirectories }).isNull()
    }
}

data class ExpectedFileExt(
    var name: String? = null,
    var content: List<String>? = null
)
fun assertFileExt(given: File, expectedOv: ExpectedFileExt.() -> Unit) {
    val diff = diffFile(given, expectedOv)
    assertThat(diff)
        .withFailMessage { "File `${given.name.value}` is different: $diff" }
        .isEmpty()
}

fun diffFile(given: File, expectedOv: ExpectedFileExt.() -> Unit): String {
    val expected = ExpectedFileExt().apply(expectedOv)
    val diff = mutableListOf<String>()

    if (expected.name != null) {
        if (given.name.value != expected.name) {
            diff.add("Different name: `${given.name.value}` != `${expected.name}`")
        }
    }

    if (expected.content != null) {
        expected.content!!.forEachIndexed { index, line ->
            if (given.content.lines.getOrNull(index) != line) {
                diff.add("Different content in line ${index + 1}: `${given.content.lines.getOrNull(index)}` != `$line`")
            }
        }
    }

    return diff.joinToString("\n")
}

data class ExpectedCompareResultExt(
    var same: Boolean? = null,
    var difference: String? = null,
    var differences: List<String>? = null
)
fun assertCompareResultExt(given: CompareResult, expectedInit: ExpectedCompareResultExt.() -> Unit) {
    val expected = ExpectedCompareResultExt().apply(expectedInit)
    if (expected.same != null) {
        assertThat(given.same).isEqualTo(expected.same)
    }
    if (expected.difference != null) {
        assertThat(given.differences).containsExactly(expected.difference)
    }
    if (expected.differences != null) {
        assertThat(given.differences).containsExactlyInAnyOrderElementsOf(expected.differences)
    }
}
