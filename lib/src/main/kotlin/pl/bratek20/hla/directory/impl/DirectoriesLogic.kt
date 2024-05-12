package pl.bratek20.hla.directory.impl

import pl.bratek20.hla.directory.api.*

class DirectoriesLogic: Directories {
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

    override fun deleteDirectory(path: Path) {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        if(!file.exists() || !file.isDirectory()) {
            return
        }

        file.deleteRecursively()
    }

    override fun write(path: Path, directory: Directory) {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        val newDir = java.io.File(file, directory.name)
        if (!newDir.exists()) {
            check(newDir.mkdirs()) {
                "Could not create directory: $newDir"
            }
        }

        directory.files.forEach {
            val newFile = java.io.File(newDir, it.name)
            newFile.writeText(it.content.lines.joinToString("\n"))
        }

        directory.directories.forEach {
            write(Path(newDir.absolutePath), it)
        }
    }

    override fun compare(dir1: Directory, dir2: Directory): CompareResult {
        val differences = compareDirectories("", dir1, dir2)

        return CompareResult(
            same = differences.isEmpty(),
            differences = differences
        )
    }

    private fun fullPath(path: String, name: String) = if (path.isEmpty()) name else "$path/$name"

    private fun compareDirectories(path: String, dir1: Directory, dir2: Directory): List<String> {
        val differences = mutableListOf<String>()
        if (dir1.name != dir2.name) {
            differences.add("Different directory names: ${fullPath(path, dir1.name)} != ${fullPath(path, dir2.name)}")
        }

        val newPath = fullPath(path, dir1.name)
        differences.addAll(compareFiles(newPath, dir1.files, dir2.files))

        val dir1Map = dir1.directories.associateBy { it.name }
        val dir2Map = dir2.directories.associateBy { it.name }

        val allDirs = dir1Map.keys + dir2Map.keys
        for (dir in allDirs) {
            val subDir1 = dir1Map[dir]
            val subDir2 = dir2Map[dir]

            if (subDir1 == null) {
                differences.add("Directory ${fullPath(newPath, dir)} not found in first directory")
            } else if (subDir2 == null) {
                differences.add("Directory ${fullPath(newPath, dir)} not found in second directory")
            } else {
                differences.addAll(compareDirectories(newPath, subDir1, subDir2))
            }
        }

        return differences
    }

    private fun compareFiles(path: String, files1: List<File>, files2: List<File>): List<String> {
        val differences = mutableListOf<String>()
        val files1Map = files1.associateBy { it.name }
        val files2Map = files2.associateBy { it.name }

        val allFiles = files1Map.keys + files2Map.keys
        for (file in allFiles) {
            val file1 = files1Map[file]
            val file2 = files2Map[file]

            val filePath = fullPath(path, file)
            if (file1 == null) {
                differences.add("File $filePath not found in first directory")
            } else if (file2 == null) {
                differences.add("File $filePath not found in second directory")
            } else {
                differences.addAll(compareFileContent(filePath, file1.content, file2.content))
            }
        }

        return differences
    }

    private fun compareFileContent(filePath: String, content1: FileContent, content2: FileContent): List<String> {
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

    private fun toApiFile(file: java.io.File): File {
        return File(
            name = file.name,
            content = FileContent(file.readLines())
        )
    }
}