package pl.bratek20.hla.facade.impl

import pl.bratek20.hla.facade.api.GenerateModuleArgs
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.parsing.impl.ModuleDefinitionsParserImpl
import pl.bratek20.hla.writing.api.ModuleWriter

class HlaFacadeImpl(
    private val generator: ModuleGenerator,
    private val writer: ModuleWriter,
) : HlaFacade {
    override fun generateModule(args: GenerateModuleArgs) {
        val parser = ModuleDefinitionsParserImpl()

        val modules = parser.parse(args.hlaFolderPath)
        val generateResult = generator.generate(args.moduleName, args.language, modules)

        writer.write(args.projectPath, generateResult)
    }
}

