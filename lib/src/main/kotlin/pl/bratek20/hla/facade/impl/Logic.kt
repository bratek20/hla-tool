package pl.bratek20.hla.facade.impl

import pl.bratek20.architecture.properties.api.Properties
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.generation.api.GenerateArgs
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.parsing.impl.ModuleDefinitionsParserLogic
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class HlaFacadeLogic(
    private val generator: ModuleGenerator,
    private val writer: ModuleWriter,
    private val properties: Properties
): HlaFacade {
    override fun startModule(args: ModuleOperationArgs): Unit {
        generateModule(args, false)
    }

    override fun updateModule(args: ModuleOperationArgs): Unit {
        generateModule(args, true)
    }

    private fun generateModule(args: ModuleOperationArgs, onlyUpdate: Boolean) {
        val parser = ModuleDefinitionsParserLogic()

        val profile = properties.get(InMemoryPropertiesSource.name, PROPERTIES_KEY, HlaProperties::class.java)
            .profiles.first { it.getName() == args.profileName }

        val modules = parser.parse(args.hlaFolderPath)

        val generateResult = generator.generate(GenerateArgs(
            moduleName = args.moduleName,
            modules = modules,
            onlyUpdate = onlyUpdate,
            profile = profile
        ))

        writer.write(
            WriteArgs(
                generateResult = generateResult,
                profile = profile
            )
        )
    }
}