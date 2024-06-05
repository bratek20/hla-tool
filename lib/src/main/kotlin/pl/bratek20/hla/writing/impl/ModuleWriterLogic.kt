package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs
import pl.bratek20.utils.logs.api.Logger

class ModuleWriterLogic(
    private val directories: Directories,
    private val files: Files,
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val rootPath = args.hlaFolderPath.add(args.profile.getProjectPath())
        val generateResult = args.generateResult
        val profile = args.profile

        val fullMainPath = rootPath.add(profile.getMainPath())
        val fullFixturesPath = rootPath.add(profile.getFixturesPath())
        val fullTestPath = rootPath.add(profile.getTestsPath())

        directories.write(fullMainPath, generateResult.main)
        directories.write(fullFixturesPath, generateResult.fixtures)
        if (generateResult.tests != null) {
            directories.write(fullTestPath, generateResult.tests)
        }

        val moduleName = generateResult.main.name.value

        if (profile.language == ModuleLanguage.TYPE_SCRIPT && profile.typeScript != null) {
            val fullMainTsconfigPath = rootPath.add(profile.typeScript.getMainTsconfigPath())
            val fullTestTsconfigPath = rootPath.add(profile.typeScript.getTestTsconfigPath())

            files.read(fullMainTsconfigPath).let {
                val currentLines = it.content.lines.toMutableList()
                val x = extract(generateResult.main)
                val newLines = mutableListOf<String>()
                val prefix = "Src/$moduleName/"
                x.forEach { something ->
                    something.fileNames.forEach { fileName ->
                        newLines.add("    \"$prefix${something.submoduleName}/$fileName\",")
                    }
                    newLines.add("")
                }
                newLines.removeAt(newLines.size - 1)

                currentLines.addAll(2, newLines)
                files.write(fullMainTsconfigPath, File(it.name, FileContent(currentLines)))
            }

            files.read(fullTestTsconfigPath).let {
                val currentLines = it.content.lines.toMutableList()
                val x = extract(generateResult.fixtures)
                val newLines = mutableListOf<String>()
                val prefix = "$moduleName/"
                x.forEach { something ->
                    something.fileNames.forEach { fileName ->
                        newLines.add("    \"$prefix${something.submoduleName}/$fileName\",")
                    }
                    newLines.add("")
                }

                if (generateResult.tests != null) {
                    val y = extract(generateResult.tests)
                    y.forEach { something ->
                        something.fileNames.forEach { fileName ->
                            newLines.add("    \"$prefix${something.submoduleName}/$fileName\",")
                        }
                        newLines.add("")
                    }

                }
                newLines.removeAt(newLines.size - 1)

                currentLines.addAll(2, newLines)
                files.write(fullTestTsconfigPath, File(it.name, FileContent(currentLines)))
            }
        }

        //test helping
        val dirs = DirectoriesLogic()

        if (profile.language == ModuleLanguage.KOTLIN && moduleName.lowercase() == "SomeModule".lowercase()) {
            val debugPath = Path("../tmp")
            dirs.delete(debugPath)
            dirs.write(debugPath, generateResult.main)
            dirs.write(debugPath, generateResult.fixtures)
            if (generateResult.tests != null)
                dirs.write(debugPath, generateResult.tests)
        }
    }

    data class Something(
        val submoduleName: String,
        val fileNames: List<String>
    )
    fun extract(dir: Directory): List<Something> {
        val result = mutableListOf<Something>()
        dir.directories.forEach { subDir ->
            val submoduleName = subDir.name.value
            val fileNames = subDir.files.map { it.name.value }
            result.add(Something(submoduleName, fileNames))
        }
        return result
    }
}