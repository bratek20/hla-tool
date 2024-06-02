package pl.bratek20.hla.directory

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.directory.api.CompareResult
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path

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

