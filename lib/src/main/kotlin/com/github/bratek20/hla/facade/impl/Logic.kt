package com.github.bratek20.hla.facade.impl

import com.github.bratek20.logs.api.Logger
import com.github.bratek20.hla.directory.api.Directory
import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.GenerateArgs
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.writing.api.ModuleWriter
import com.github.bratek20.hla.writing.api.WriteArgs

class HlaFacadeLogic(
    private val parser: ModuleGroupParser,
    private val generator: ModuleGenerator,
    private val writer: ModuleWriter,
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
        val group = parseGroup(args.getHlaFolderPath(), args.getProfileName())

        group.getModules().forEach {
            postPrepareGenerateModule(it.getName(), group, false, args.getHlaFolderPath())
        }
    }

    override fun updateAllModules(args: AllModulesOperationArgs) {
        val group = parseGroup(args.getHlaFolderPath(), args.getProfileName())

        group.getModules().forEach {
            postPrepareGenerateModule(it.getName(), group, true, args.getHlaFolderPath())
        }
    }

    private fun generateModule(args: ModuleOperationArgs, onlyUpdate: Boolean, hlaFolderPath: Path) {
        val group = parseGroup(args.getHlaFolderPath(), args.getProfileName())

        postPrepareGenerateModule(args.getModuleName(), group, onlyUpdate, hlaFolderPath)
    }

    private fun postPrepareGenerateModule(
        moduleName: ModuleName,
        group: ModuleGroup,
        onlyUpdate: Boolean,
        hlaFolderPath: Path
    ) {
        val generateResult = generator.generate(GenerateArgs.create(
            moduleToGenerate = moduleName,
            group = group,
            onlyUpdate = onlyUpdate,
        ))

        val suffix = if (onlyUpdate) "updated" else "generated"
        logFile(generateResult.getMain(), suffix)
        logFile(generateResult.getFixtures(), suffix)
        logFile(generateResult.getTests(), suffix)

        writer.write(
            WriteArgs.create(
                hlaFolderPath = hlaFolderPath,
                generateResult = generateResult,
                profile = group.getProfile(),
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

    private fun parseGroup(hlaFolderPath: Path, profileName: ProfileName): ModuleGroup {
        return parser.parse(hlaFolderPath, profileName)
    }
}