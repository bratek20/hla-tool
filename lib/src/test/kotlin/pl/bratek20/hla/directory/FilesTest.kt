package pl.bratek20.hla.directory

import org.junit.jupiter.api.Test
import pl.bratek20.architecture.context.someContextBuilder
import pl.bratek20.architecture.exceptions.assertApiExceptionThrown
import pl.bratek20.hla.directory.api.FileNotFoundException
import pl.bratek20.hla.directory.api.Files
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoryImpl
import pl.bratek20.hla.directory.fixtures.assertFile
import pl.bratek20.hla.directory.fixtures.file

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
                message = "File not found: $filePath"
            }
        )

        val file = file {
            name = fileName
            content = "abc"
        }
        files.write(filePath, file)

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