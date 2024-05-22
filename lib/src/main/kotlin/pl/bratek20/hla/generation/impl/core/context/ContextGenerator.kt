package pl.bratek20.hla.generation.impl.core.context

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.generation.impl.core.api.InterfaceViewFactory

class ImplContextGenerator: FileGenerator() {
    override fun name(): String {
        return "Impl"
    }

    override fun generateFileContent(): FileContent {
        val factory = InterfaceViewFactory(apiTypeFactory)
        return contentBuilder("impl.vm")
            .put("interfaces", factory.create(module.interfaces))
            .build()
    }
}

class ContextGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Context"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun velocityDirPath(): String {
        return "context"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return module.interfaces.isNotEmpty()
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            ImplContextGenerator()
        )
    }
}