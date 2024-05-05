package pl.bratek20.hla.facade.impl

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.facade.api.GeneratedModuleArgs
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl
import pl.bratek20.hla.parsing.impl.ModuleDefinitionsParserImpl

class HlaFacadeImpl(
    private val directories: Directories
) : HlaFacade {
    override fun generateModule(args: GeneratedModuleArgs) {
        val parser = ModuleDefinitionsParserImpl()
        val generator = ModuleGeneratorImpl()

        val modules = parser.parse(args.inPath)
        val moduleDirectory = generator.generate(args.moduleName, args.language, modules)
        directories.write(args.outPath, moduleDirectory)
    }
}

class FacadeContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(HlaFacade::class.java, HlaFacadeImpl::class.java)
    }
}