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

        if (profile.language == ModuleLanguage.TYPE_SCRIPT && profile.typeScript != null) {
            val fullMainTsconfigPath = rootPath.add(profile.typeScript.getMainTsconfigPath())
            val fullTestTsconfigPath = rootPath.add(profile.typeScript.getTestTsconfigPath())

            files.read(fullMainTsconfigPath).let {
                val newLines = it.content.lines.toMutableList()
                newLines.add(2, "    \"Src/OtherModule/Api/NamedTypes.ts\",")
                newLines.add(3, "    \"Src/OtherModule/Api/Properties.ts\",")
                newLines.add(4, "    \"Src/OtherModule/Api/Data.ts\",")
                newLines.add(5, "    \"Src/OtherModule/Api/ValueObjects.ts\",")
                newLines.add(6, "")
                newLines.add(7, "    \"Src/OtherModule/Web/Dtos.ts\",")
                files.write(fullMainTsconfigPath, File(it.name, FileContent(newLines)))
            }

            files.read(fullTestTsconfigPath).let {
                val newLines = it.content.lines.toMutableList()
                newLines.add(2, "    \"OtherModule/Fixtures/Builders.ts\",")
                newLines.add(3, "    \"OtherModule/Fixtures/Diffs.ts\",")
                newLines.add(4, "    \"OtherModule/Fixtures/Asserts.ts\",")
                newLines.add(5, "")
                newLines.add(6, "    \"OtherModule/Tests/ApiTest.ts\",")
                files.write(fullTestTsconfigPath, File(it.name, FileContent(newLines)))
            }
        }

        //test helping
        val dirs = DirectoriesLogic()
        val moduleName = generateResult.main.name
        if (profile.language == ModuleLanguage.KOTLIN && moduleName.value.lowercase() == "SomeModule".lowercase()) {
            val debugPath = Path("../tmp")
            dirs.delete(debugPath)
            dirs.write(debugPath, generateResult.main)
            dirs.write(debugPath, generateResult.fixtures)
            if (generateResult.tests != null)
                dirs.write(debugPath, generateResult.tests)
        }
    }
}