package com.github.bratek20.utils.directory.fixtures

import com.github.bratek20.utils.directory.api.*
import com.github.bratek20.utils.directory.impl.DirectoriesLogic
import com.github.bratek20.utils.directory.impl.FilesLogic
import org.assertj.core.api.Assertions.assertThat

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
        return DirectoriesLogic().read(path)
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

    fun assertWriteAndGetFile(writeNumber: Int, expectedPath: String, expectedName: String): File {
        val (path, file) = fileWrites[writeNumber - 1]
        assertThat(path).isEqualTo(Path(expectedPath))
        assertThat(file.getName().value).isEqualTo(expectedName)
        return file
    }

    fun assertWriteCount(expectedCount: Int) {
        assertThat(fileWrites).hasSize(expectedCount)
    }

    override fun read(path: Path): File {
        return FilesLogic().read(path)
    }

    override fun delete(path: Path) {
        TODO("Not yet implemented")
    }

    override fun compare(file1: File, file2: File): CompareResult {
        TODO("Not yet implemented")
    }
}

