package com.github.bratek20.utils.directory.impl

import com.github.bratek20.hla.directory.api.*
import com.github.bratek20.utils.directory.api.*

class DirectoriesLogic: Directories {
    override fun read(path: Path): Directory {
        val nioPath = java.nio.file.Paths.get(path.value)
        val file = nioPath.toFile()

        require(file.exists() && file.isDirectory()) {
            "File does not exist or not dir for path: $nioPath"
        }

        val allFiles = file.listFiles()?.sorted()
        val files = allFiles!!.filter { it.isFile }.map { toApiFile(it) }
        val directories = allFiles.filter { it.isDirectory }.map { read(Path(it.absolutePath)) }

        return Directory(
            name = file.name,
            files = files,
            directories = directories
        )
    }

    override fun delete(path: Path) {
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

        val newDir = java.io.File(file, directory.getName().value)
        if (!newDir.exists()) {
            check(newDir.mkdirs()) {
                "Could not create directory: $newDir"
            }
        }

        directory.getFiles().forEach {
            val newFile = java.io.File(newDir, it.getName().value)
            newFile.writeText(it.getContent().lines.joinToString("\n"))
        }

        directory.getDirectories().forEach {
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

    private fun fullPath(path: String, name: DirectoryName) = if (path.isEmpty()) name.value else "$path/${name.value}"
    private fun fullPath(path: String, name: FileName) = if (path.isEmpty()) name.value else "$path/${name.value}"

    private fun compareDirectories(path: String, dir1: Directory, dir2: Directory): List<String> {
        val differences = mutableListOf<String>()
        if (dir1.getName() != dir2.getName()) {
            differences.add("Different directory names: ${fullPath(path, dir1.getName())} != ${fullPath(path, dir2.getName())}")
        }

        val newPath = fullPath(path, dir1.getName())
        differences.addAll(compareFiles(newPath, dir1.getFiles(), dir2.getFiles()))

        val dir1Map = dir1.getDirectories().associateBy { it.getName() }
        val dir2Map = dir2.getDirectories().associateBy { it.getName() }

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
        val files1Map = files1.associateBy { it.getName() }
        val files2Map = files2.associateBy { it.getName() }

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
                differences.addAll(compareFileContent(filePath, file1.getContent(), file2.getContent()))
            }
        }

        return differences
    }



    private fun toApiFile(file: java.io.File): File {
        return File(
            name = file.name,
            content = file.readText()
        )
    }
}