package pl.bratek20.hla.directory.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.directory.api.CompareResult
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File

data class ExpectedDirectory(
    var name: String? = null,
    var files: List<ExpectedFile.() -> Unit>? = null,
    var hasFile: (ExpectedFile.() -> Unit)? = null,
    var hasDirectory: (ExpectedDirectory.() -> Unit)? = null,
)

fun assertDirectory(given: Directory, expectedOv: ExpectedDirectory.() -> Unit) {
    val expected = ExpectedDirectory().apply(expectedOv)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }

    if (expected.files != null) {
        assertThat(given.files).hasSameSizeAs(expected.files)
        expected.files!!.forEachIndexed { index, expectedFile ->
            assertFile(given.files[index], expectedFile)
        }
    }

    if (expected.hasFile != null) {
        val expectedFile = ExpectedFile().apply(expected.hasFile!!)
        val file = given.files.find { it.name == expectedFile.name }
        assertThat(file).isNotNull
        assertFile(file!!, expected.hasFile!!)
    }

    if (expected.hasDirectory != null) {
        val expectedDirectory = ExpectedDirectory().apply(expected.hasDirectory!!)
        val directory = given.directories.find { it.name == expectedDirectory.name }
        assertThat(directory).isNotNull
        assertDirectory(directory!!, expected.hasDirectory!!)
    }
}

data class ExpectedFile(
    var name: String? = null,
    var content: List<String>? = null
)
fun assertFile(given: File, expectedOv: ExpectedFile.() -> Unit) {
    val expected = ExpectedFile().apply(expectedOv)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }

    if (expected.content != null) {
        expected.content!!.forEachIndexed { index, line ->
            assertThat(given.content.lines.getOrNull(index)).isEqualTo(line)
        }
    }
}

data class ExpectedCompareResult(
    var same: Boolean? = null,
    var difference: String? = null,
    var differences: List<String>? = null
)
fun assertCompareResult(given: CompareResult, expectedInit: ExpectedCompareResult.() -> Unit) {
    val expected = ExpectedCompareResult().apply(expectedInit)
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
