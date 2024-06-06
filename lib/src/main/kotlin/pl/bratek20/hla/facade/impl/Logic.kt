package pl.bratek20.hla.facade.impl

import pl.bratek20.architecture.properties.api.Properties
import pl.bratek20.architecture.properties.sources.yaml.YamlPropertiesSource
import pl.bratek20.hla.definitions.api.ModuleDefinition
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
    private val propertiesSource: YamlPropertiesSource,
    private val properties: Properties
): HlaFacade {
    override fun startModule(args: ModuleOperationArgs): Unit {
        generateModule(args, false, args.hlaFolderPath)
    }

    override fun updateModule(args: ModuleOperationArgs): Unit {
        generateModule(args, true, args.hlaFolderPath)
    }

    override fun updateAllModules(args: AllModulesOperationArgs) {
        val (modules, profile) = prepare(args.hlaFolderPath, args.profileName)

        modules.forEach {
            postPrepareGenerateModule(it.name, modules, profile, true, args.hlaFolderPath)
        }
    }

    private fun generateModule(args: ModuleOperationArgs, onlyUpdate: Boolean, hlaFolderPath: Path) {
        val (modules, profile) = prepare(args.hlaFolderPath, args.profileName)

        postPrepareGenerateModule(args.moduleName, modules, profile, onlyUpdate, hlaFolderPath)
    }

    private fun postPrepareGenerateModule(
        moduleName: ModuleName,
        modules: List<ModuleDefinition>,
        profile: HlaProfile,
        onlyUpdate: Boolean,
        hlaFolderPath: Path
    ) {
        val generateResult = generator.generate(GenerateArgs(
            moduleName = moduleName,
            modules = modules,
            onlyUpdate = onlyUpdate,
            profile = profile
        ))

        writer.write(
            WriteArgs(
                hlaFolderPath = hlaFolderPath,
                generateResult = generateResult,
                profile = profile,
                onlyUpdate = onlyUpdate
            )
        )
    }

    private fun prepare(hlaFolderPath: Path, profileName: ProfileName): Pair<List<ModuleDefinition>, HlaProfile>{
        val parser = ModuleDefinitionsParserLogic()

        propertiesSource.propertiesPath = hlaFolderPath.value + "/properties.yaml"

        val profile = properties.get(PROFILES_KEY)
            .firstOrNull { it.getName() == profileName }
            ?: throw IllegalArgumentException("Profile with name $profileName not found")

        val modules = parser.parse(hlaFolderPath)

        return Pair(modules, profile)
    }
}