package pl.bratek20.hla.writing.impl

import pl.bratek20.architecture.properties.api.Properties
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.facade.api.HLA_PROPERTIES_KEY
import pl.bratek20.hla.facade.api.HlaProperties
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class ModuleWriterLogic(
    private val directories: Directories,
    private val properties: Properties
): ModuleWriter {

    override fun write(args: WriteArgs) {
        val projectPath = args.projectPath
        val generateResult = args.generateResult

        val hlaProperties = properties.get(
            InMemoryPropertiesSource.name,
            HLA_PROPERTIES_KEY,
            HlaProperties::class.java
        )

        var mainPathSuffix = Path("")
        var testFixturesPathSuffix = Path("")
        if (args.language == ModuleLanguage.KOTLIN) {
            hlaProperties.java.rootPackage.let {
                mainPathSuffix = Path("src/main/kotlin/" + it.replace(".", "/"))
                testFixturesPathSuffix = Path("src/testFixtures/kotlin/" + it.replace(".", "/"))
            }
        }
        if (args.language == ModuleLanguage.TYPE_SCRIPT) {
            mainPathSuffix = Path("main")
            testFixturesPathSuffix = Path("test")
        }

        val mainPath = projectPath.add(mainPathSuffix)
        val testFixturesPath = projectPath.add(testFixturesPathSuffix)

        directories.write(mainPath, generateResult.main)
        directories.write(testFixturesPath, generateResult.testFixtures)
    }
}