package pl.bratek20.hla.facade.impl

import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.parsing.impl.ModuleDefinitionsParserLogic
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.api.WriteArgs

class HlaFacadeImpl(
    private val generator: ModuleGenerator,
    private val writer: ModuleWriter,
): HlaFacade {
    override fun startModule(args: ModuleOperationArgs): Unit {
        generateModule(args)
    }

    override fun updateModule(args: ModuleOperationArgs): Unit {
        generateModule(args)
    }

    private fun generateModule(args: ModuleOperationArgs) {
        val parser = ModuleDefinitionsParserLogic()

        val modules = parser.parse(args.hlaFolderPath)
        val generateResult = generator.generate(args.moduleName, args.language, modules)

        writer.write(
                WriteArgs(
                projectPath = args.projectPath,
                generateResult = generateResult,
                language = args.language
            )
        )
    }
}