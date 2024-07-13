package com.github.bratek20.utils.directory

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.context.DirectoryImpl
import com.github.bratek20.utils.directory.fixtures.assertCompareResult
import com.github.bratek20.utils.directory.fixtures.assertDirectory
import com.github.bratek20.utils.directory.fixtures.directory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class DirectoriesTest: TempDirTest() {
    private val directories = someContextBuilder()
        .withModules(DirectoryImpl())
        .get(Directories::class.java)

    @Test
    fun shouldWrite() {
        val dir = directory {
            name = "dir"
            files = listOf {
                name = "someFile.txt"
                content = "abc"
            }
        }
        directories.write(tempDir, dir)

        val result = directories.read(tempDir)
        assertDirectory(result) {
            directories = listOf {
                name = "dir"
                files = listOf {
                    name = "someFile.txt"
                    content = "abc"
                }
            }
        }
    }

    @Test
    fun shouldOverwriteFiles() {
        val dir = directory {
            name = "dir"
            files = listOf {
                name = "someFile.txt"
                content = "abc"
            }
        }
        directories.write(tempDir, dir)

        val updatedDir = directory {
            name = "dir"
            files = listOf {
                name = "someFile.txt"
                content = "def"
            }
        }
        directories.write(tempDir, updatedDir)

        val result = directories.read(tempDir)
        assertDirectory(result) {
            directories = listOf {
                name = "dir"
                files = listOf {
                    name = "someFile.txt"
                    content = "def"
                }
            }
        }
    }

    @Test
    fun shouldReadDirectory() {
        val result = directories.read(Path("src/test/resources/directory/somedir"))
        assertDirectory(result) {
            name = "somedir"
            directories = listOf {
                name = "subdir"
                files = listOf {
                    name = "SomeFile.kt"
                    content = "package directory\n" +
                         "\n" +
                        "class SomeFile {\n" +
                        "}"
                }
            }
        }
    }

    @Test
    fun shouldReadDirectoryFilesInCorrectOrder() {
        val result = directories.read(Path("src/test/resources/directory/multiple-modules-order"))
        assertDirectory(result) {
            name = "multiple-modules-order"
            files = listOf(
                {
                    name = "ModuleA.module"
                },
                {
                    name = "ModuleB.module"
                },
                {
                    name = "ModuleC.module"
                }
            )
        }
    }

    @Test
    fun shouldReadCorrectFileLength() {
        //TODO api.readFile
        val result = directories.read(Path("src/test/resources/directory/somedir/subdir"))
        //TODO proper setup for this assertion
        assertThat(result.getFiles()[0].getContent().lines.size).isEqualTo(4)
    }

    @Test
    fun shouldCompareDirectoryFiles() {
        val dir1 = directory {
            name = "dir1"
            files = listOf {
                name = "file1"
                content = "content1"
            }
        }
        assertCompareResult(directories.compare(dir1, dir1)) {
            same = true
        }

        val wrongFileName = directory {
            name = "dir1"
            files = listOf {
                name = "file2"
                content = "content1"
            }
        }
        assertCompareResult(directories.compare(dir1, wrongFileName)) {
            differences = listOf(
                "File dir1/file1 not found in second directory",
                "File dir1/file2 not found in first directory"
            )
        }

        val wrongFileContent = directory {
            name = "dir1"
            files = listOf {
                name = "file1"
                content = "content2"
            }
        }
        assertCompareResult(directories.compare(dir1, wrongFileContent)) {
            differences = listOf("Different content for file dir1/file1 in line 1: `content1` != `content2`")
        }
    }

    @Test
    fun shouldCompareNestedDirectories() {
        val dir = directory {
            name = "dir"
            directories = listOf {
                name = "dir1"
                files = listOf {
                    name = "file1"
                }
            }
        }
        assertCompareResult(directories.compare(dir, dir)) {
            same = true
        }

        val missingDirectory = directory {
            name = "dir"
        }
        assertCompareResult(directories.compare(dir, missingDirectory)) {
            differences = listOf("Directory dir/dir1 not found in second directory")
        }

        val wrongName = directory {
            name = "otherDir"
            directories = listOf {
                name = "dir1"
                files = listOf {
                    name = "file1"
                }
            }
        }
        assertCompareResult(directories.compare(dir, wrongName)) {
            differences = listOf("Different directory names: dir != otherDir")
        }

        val wrongNestedName = directory {
            name = "dir"
            directories = listOf {
                name = "dir2"
                files = listOf {
                    name = "file1"
                }
            }
        }
        assertCompareResult(directories.compare(dir, wrongNestedName)) {
            differences = listOf(
                "Directory dir/dir1 not found in second directory",
                "Directory dir/dir2 not found in first directory"
            )
        }

        val wrongFile = directory {
            name = "dir"
            directories = listOf {
                name = "dir1"
                files = listOf {
                    name = "file2"
                }
            }
        }
        assertCompareResult(directories.compare(dir, wrongFile)) {
            differences = listOf(
                "File dir/dir1/file1 not found in second directory",
                "File dir/dir1/file2 not found in first directory"
            )
        }
    }
}