package pl.bratek20.hla.directory

import org.assertj.core.api.Assertions.assertThat

data class ExpectedDirectory(
    var name: String? = null,
)

fun assertDirectory(given: Directory, expectedOv: ExpectedDirectory.() -> Unit) {
    val expected = ExpectedDirectory().apply(expectedOv)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }
}

data class ExpectedFile(
    var name: String? = null,
    var content: String? = null
)
fun assertFile(given: File, expectedOv: ExpectedFile.() -> Unit) {
    val expected = ExpectedFile().apply(expectedOv)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }

    if (expected.content != null) {
        assertThat(given.content).isEqualTo(expected.content)
    }
}