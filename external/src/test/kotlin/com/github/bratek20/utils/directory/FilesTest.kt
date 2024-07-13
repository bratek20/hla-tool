package com.github.bratek20.utils.directory

import org.junit.jupiter.api.Test
import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.utils.directory.api.FileNotFoundException
import com.github.bratek20.utils.directory.api.Files
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.context.DirectoryImpl
import com.github.bratek20.utils.directory.fixtures.assertFile
import com.github.bratek20.utils.directory.fixtures.file

class FilesTest: TempDirTest() {
    private val files = someContextBuilder()
        .withModules(DirectoryImpl())
        .get(Files::class.java)

    @Test
    fun shouldWork() {
        val fileName = "someFile.txt"
        val filePath = tempDir.add(Path(fileName))

        assertApiExceptionThrown(
            {files.read(filePath)},
            {
                type = FileNotFoundException::class
                message = "File not found: ${filePath.value}"
            }
        )

        val file = file {
            name = fileName
            content = "abc"
        }
        files.write(tempDir, file)

        val result = files.read(filePath)
        assertFile(result) {
            name = fileName
            content = "abc"
        }

        files.delete(filePath)
        assertApiExceptionThrown(
            {files.read(filePath)},
            {
                type = FileNotFoundException::class
            }
        )
    }
}