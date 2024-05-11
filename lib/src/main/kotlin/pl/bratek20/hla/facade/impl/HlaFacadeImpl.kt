package pl.bratek20.hla.facade.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.facade.api.GenerateModuleArgs
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.parsing.impl.ModuleDefinitionsParserImpl

class HlaFacadeImpl(
    private val generator: ModuleGenerator,
    private val directories: Directories,
) : HlaFacade {
    override fun generateModule(args: GenerateModuleArgs) {
        val parser = ModuleDefinitionsParserImpl()

        val modules = parser.parse(args.inPath)
        val moduleDirectory = generator.generate(args.moduleName, args.language, modules)
        directories.write(args.outPath, moduleDirectory)
    }
}

