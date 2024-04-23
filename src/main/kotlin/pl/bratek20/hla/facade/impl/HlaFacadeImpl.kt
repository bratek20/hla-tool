package pl.bratek20.hla.facade.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.facade.api.GeneratedModuleArgs
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl
import pl.bratek20.hla.parsing.impl.HlaModulesParserImpl

class HlaFacadeImpl(
    private val directories: Directories
) : HlaFacade {
    override fun generateModule(args: GeneratedModuleArgs) {
        val parser = HlaModulesParserImpl()
        val generator = ModuleGeneratorImpl()

        val modules = parser.parse(args.inPath)
        val moduleDirectory = generator.generate(args.moduleName, args.language, modules)
        directories.write(args.outPath, moduleDirectory)
    }
}