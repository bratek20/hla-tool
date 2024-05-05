package pl.bratek20.hla.directory

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.directory.api.CompareResult
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path

class DirectoriesMock: Directories {
    private val directoryWrites: MutableList<Pair<Path, Directory>> = mutableListOf()

    override fun write(path: Path, directory: Directory) {
        directoryWrites.add(path to directory)
    }

    fun assertOneWriteAndGetDirectory(expectedPath: String): Directory {
        assertThat(directoryWrites).hasSize(1)
        val (path, directory) = directoryWrites[0]
        assertThat(path).isEqualTo(Path(expectedPath))
        return directory
    }

    override fun readDirectory(path: Path): Directory {
        TODO("Not yet implemented")
    }

    override fun deleteDirectory(path: Path) {
        TODO("Not yet implemented")
    }



    override fun compare(dir1: Directory, dir2: Directory): CompareResult {
        TODO("Not yet implemented")
    }
}

class DirectoriesMockContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(Directories::class.java, DirectoriesMock::class.java)
    }
}