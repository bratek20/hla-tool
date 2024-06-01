package pl.bratek20.hla.directory.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.directory.api.Directory

data class ExpectedDirectoryExt(
    var hasNoDirectories: String? = null,
)

fun assertDirectoryExt(given: Directory, expectedOv: ExpectedDirectoryExt.() -> Unit) {
    val expected = ExpectedDirectoryExt().apply(expectedOv)

    if (expected.hasNoDirectories != null) {
        assertThat(given.directories.find { it.name.value == expected.hasNoDirectories }).isNull()
    }
}
