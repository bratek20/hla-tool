package pl.bratek20.hla.directory.impl

import pl.bratek20.hla.directory.api.*

class FilesLogic: Files {
    override fun write(path: Path, file: File) {
        val nioPath = java.nio.file.Paths.get(path.add(Path(file.name.value)).value)
        val nioFile = nioPath.toFile()

        nioFile.writeText(file.content.toString())
    }

    override fun read(path: Path): File {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("File not found: $path")
        }

        return File(
            name = FileName(file.name),
            content = FileContent(file.readLines())
        )
    }

    override fun delete(path: Path) {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        if (!file.exists() || !file.isFile) {
            return
        }

        file.delete()
    }

    override fun compare(file1: File, file2: File): CompareResult {
        TODO()
    }
}