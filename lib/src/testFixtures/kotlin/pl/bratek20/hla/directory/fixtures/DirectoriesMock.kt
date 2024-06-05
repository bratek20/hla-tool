package pl.bratek20.hla.directory.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.directory.api.*

class DirectoriesMock: Directories {
    private val directoryWrites: MutableList<Pair<Path, Directory>> = mutableListOf()

    override fun write(path: Path, dir: Directory) {
        directoryWrites.add(path to dir)
    }

    fun assertWriteAndGetDirectory(writeNumber: Int, expectedPath: String): Directory {
        val (path, directory) = directoryWrites[writeNumber - 1]
        assertThat(path).isEqualTo(Path(expectedPath))
        return directory
    }

    fun assertWriteCount(expectedCount: Int) {
        assertThat(directoryWrites).hasSize(expectedCount)
    }

    override fun read(path: Path): Directory {
        TODO("Not yet implemented")
    }

    override fun delete(path: Path) {
        TODO("Not yet implemented")
    }

    override fun compare(dir1: Directory, dir2: Directory): CompareResult {
        TODO("Not yet implemented")
    }
}

class FilesMock: Files {
    private val fileWrites: MutableList<Pair<Path, File>> = mutableListOf()

    override fun write(path: Path, file: File) {
        fileWrites.add(path to file)
    }

    fun assertWriteAndGetFile(writeNumber: Int, expectedPath: String): File {
        val (path, file) = fileWrites[writeNumber - 1]
        assertThat(path).isEqualTo(Path(expectedPath))
        return file
    }

    fun assertWriteCount(expectedCount: Int) {
        assertThat(fileWrites).hasSize(expectedCount)
    }

    override fun read(path: Path): File {
        TODO("Not yet implemented")
    }

    override fun delete(path: Path) {
        TODO("Not yet implemented")
    }

    override fun compare(file1: File, file2: File): CompareResult {
        TODO("Not yet implemented")
    }
}

