package com.github.bratek20.utils.directory.fixtures

import org.assertj.core.api.Assertions.assertThat
import com.github.bratek20.utils.directory.api.Directory

data class ExpectedDirectoryExt(
    var hasNoDirectories: String? = null,
)

fun assertDirectoryExt(given: Directory, expectedOv: ExpectedDirectoryExt.() -> Unit) {
    val expected = ExpectedDirectoryExt().apply(expectedOv)

    if (expected.hasNoDirectories != null) {
        assertThat(given.getDirectories().find { it.getName().value == expected.hasNoDirectories }).isNull()
    }
}
