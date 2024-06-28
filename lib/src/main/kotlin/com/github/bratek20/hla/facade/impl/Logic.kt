package com.github.bratek20.hla.facade.impl

import com.github.bratek20.logs.api.Logger
import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.properties.sources.yaml.YamlPropertiesSource
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.directory.api.Directory
import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.GenerateArgs
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.parsing.impl.ModuleGroupParserLogic
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs

class HlaFacadeLogic(
    private val generator: ModuleGenerator,
    private val writer: ModuleWriter,
    private val propertiesSource: YamlPropertiesSource,
    private val properties: Properties,
    private val logger: Logger
): HlaFacade {
    override fun startModule(args: ModuleOperationArgs): Unit {
        logger.info("Starting module ${args.getModuleName().value} with profile ${args.getProfileName().value}")
        generateModule(args, false, args.getHlaFolderPath())
    }

    override fun updateModule(args: ModuleOperationArgs): Unit {
        logger.info("Updating module ${args.getModuleName().value} with profile ${args.getProfileName().value}")
        generateModule(args, true, args.getHlaFolderPath())
    }

    override fun startAllModules(args: AllModulesOperationArgs) {
        val (modules, profile) = prepare(args.getHlaFolderPath(), args.getProfileName())

        modules.forEach {
            postPrepareGenerateModule(it.getName(), modules, profile, false, args.getHlaFolderPath())
        }
    }

    override fun updateAllModules(args: AllModulesOperationArgs) {
        val (modules, profile) = prepare(args.getHlaFolderPath(), args.getProfileName())

        modules.forEach {
            postPrepareGenerateModule(it.getName(), modules, profile, true, args.getHlaFolderPath())
        }
    }

    private fun generateModule(args: ModuleOperationArgs, onlyUpdate: Boolean, hlaFolderPath: Path) {
        val (modules, profile) = prepare(args.getHlaFolderPath(), args.getProfileName())

        postPrepareGenerateModule(args.getModuleName(), modules, profile, onlyUpdate, hlaFolderPath)
    }

    private fun postPrepareGenerateModule(
        moduleName: ModuleName,
        modules: List<ModuleDefinition>,
        profile: HlaProfile,
        onlyUpdate: Boolean,
        hlaFolderPath: Path
    ) {
        val generateResult = generator.generate(GenerateArgs(
            moduleName = moduleName.value,
            modules = modules,
            onlyUpdate = onlyUpdate,
            profile = profile
        ))

        val suffix = if (onlyUpdate) "updated" else "generated"
        logFile(generateResult.getMain(), suffix)
        logFile(generateResult.getFixtures(), suffix)
        logFile(generateResult.getTests(), suffix)

        writer.write(
            WriteArgs(
                hlaFolderPath = hlaFolderPath.value,
                generateResult = generateResult,
                profile = profile,
                onlyUpdate = onlyUpdate
            )
        )
    }

    private fun logFile(directory: Directory?, suffix: String) {
        directory?.getDirectories()?.forEach { dir ->
            dir.getFiles().forEach {
                logger.info("${dir.getName().value}/${it.getName().value} $suffix", this)
            }
        }
    }

    private fun prepare(hlaFolderPath: Path, profileName: ProfileName): Pair<List<ModuleDefinition>, HlaProfile>{
        val parser = ModuleGroupParserLogic()

        propertiesSource.propertiesPath = hlaFolderPath.value + "/properties.yaml"

        val profile = properties.get(PROFILES_KEY)
            .firstOrNull { it.getName() == profileName }
            ?: throw ProfileNotFoundException("Profile with name $profileName not found")

        val modules = parser.parse(hlaFolderPath, profileName).getModules()
        return Pair(modules, profile)
    }
}