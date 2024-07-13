package com.github.bratek20.hla.directory.impl

import com.github.bratek20.hla.directory.api.*

fun compareFileContent(filePath: String, content1: FileContent, content2: FileContent): List<String> {
    if (content1.lines.size != content2.lines.size) {
        return listOf("Different number of lines for file $filePath: ${content1.lines.size} != ${content2.lines.size}")
    }

    return content1.lines.zip(content2.lines).mapIndexed { index, (line1, line2) ->
        if (line1 != line2) {
            "Different content for file $filePath in line ${index + 1}: `$line1` != `$line2`"
        } else {
            null
        }
    }.filterNotNull()
}

class FilesLogic: Files {
    override fun write(path: Path, file: File) {
        val nioPath = java.nio.file.Paths.get(path.add(Path(file.getName().value)).value)
        val nioFile = nioPath.toFile()

        nioFile.writeText(file.getContent().toString())
    }

    override fun read(path: Path): File {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("File not found: $path")
        }

        return File(
            name = file.name,
            content = file.readText()
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
        val differences = compareFileContent(file1.getName().value, file1.getContent(), file2.getContent())

        return CompareResult(
            differences.isEmpty(),
            differences
        )
    }
}