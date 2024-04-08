package pl.bratek20.hla.directory.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.bratek20.hla.directory.assertCompareResult
import pl.bratek20.hla.directory.assertDirectory
import pl.bratek20.hla.directory.directory
import pl.bratek20.hla.directory.impl.DirectoryLogic

class DirectoryApiTest {
    private val api = DirectoryLogic()

    @Test
    fun shouldReadDirectory() {
        val result = api.readDirectory(Path("example/src/main/java/pl/bratek20/somemodule"))
        assertDirectory(result) {
            name = "somemodule"
            hasFile = {
                name = "SomeId.kt"
                content = listOf(
                    "package pl.bratek20.somemodule",
                    "",
                    "data class SomeId(",
                    "    val value: String",
                    ")"
                )
            }
        }
    }

    @Test
    fun shouldCompareDirectories() {
        val dir1 = directory {
            name = "dir1"
            files = listOf {
                name = "file1"
                content = "content1"
            }
        }
        assertCompareResult(api.compare(dir1, dir1)) {
            same = true
        }

        val wrongName = directory {
            name = "dir2"
            files = listOf {
                name = "file1"
                content = "content1"
            }
        }
        assertCompareResult(api.compare(dir1, wrongName)) {
            difference = "Different names: dir1 != dir2"
        }

        val wrongFileName = directory {
            name = "dir1"
            files = listOf {
                name = "file2"
                content = "content1"
            }
        }
        assertCompareResult(api.compare(dir1, wrongFileName)) {
            differences = listOf(
                "File file1 not found in second directory",
                "File file2 not found in first directory"
            )
        }

        val wrongFileContent = directory {
            name = "dir1"
            files = listOf {
                name = "file1"
                content = "content2"
            }
        }
        assertCompareResult(api.compare(dir1, wrongFileContent)) {
            difference = "Different content for file file1 in line 1: content1 != content2"
        }
    }
}