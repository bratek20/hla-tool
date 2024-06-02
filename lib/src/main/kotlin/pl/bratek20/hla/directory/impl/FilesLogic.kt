package pl.bratek20.hla.directory.impl

import pl.bratek20.hla.directory.api.CompareResult
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.Files
import pl.bratek20.hla.directory.api.Path

class FilesLogic: Files {
    override fun write(path: Path, file: File) {
        TODO("Not yet implemented")
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