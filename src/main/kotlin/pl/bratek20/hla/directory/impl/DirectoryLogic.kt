package pl.bratek20.hla.directory.impl

import pl.bratek20.hla.directory.api.*

class DirectoryLogic: DirectoryApi {
    override fun readDirectory(path: Path): Directory {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        require(file.exists() && file.isDirectory()) {
            "File does not exist or not dir for path: $nioPath"
        }

        val allFiles = file.listFiles()
        val files = allFiles!!.filter { it.isFile }.map { toApiFile(it) }
        val directories = allFiles.filter { it.isDirectory }.map { readDirectory(Path(it.absolutePath)) }

        return Directory(
            name = file.name,
            files = files,
            directories = directories
        )
    }

    override fun compare(dir1: Directory, dir2: Directory): CompareResult {
        val differences = mutableListOf<String>()
        if (dir1.name != dir2.name) {
            differences.add("Different names: ${dir1.name} != ${dir2.name}")
        }

        differences.addAll(compareFiles(dir1.files, dir2.files))
        return CompareResult(
            same = differences.isEmpty(),
            differences = differences
        )
    }

    private fun compareFiles(files1: List<File>, files2: List<File>): List<String> {
        val differences = mutableListOf<String>()
        val files1Map = files1.associateBy { it.name }
        val files2Map = files2.associateBy { it.name }

        val allFiles = files1Map.keys + files2Map.keys
        for (file in allFiles) {
            val file1 = files1Map[file]
            val file2 = files2Map[file]

            if (file1 == null) {
                differences.add("File $file not found in first directory")
            } else if (file2 == null) {
                differences.add("File $file not found in second directory")
            } else {
                differences.addAll(compareFileContent(file, file1.content, file2.content))
            }
        }

        return differences
    }

    private fun compareFileContent(name: String, content1: FileContent, content2: FileContent): List<String> {
        if (content1.lines.size != content2.lines.size) {
            return listOf("Different number of lines for file $name: ${content1.lines.size} != ${content2.lines.size}")
        }

        return content1.lines.zip(content2.lines).mapIndexed { index, (line1, line2) ->
            if (line1 != line2) {
                "Different content for file $name in line ${index + 1}: $line1 != $line2"
            } else {
                null
            }
        }.filterNotNull()
    }

    private fun toApiFile(file: java.io.File): File {
        return File(
            name = file.name,
            content = FileContent(file.readLines())
        )
    }
}