package pl.bratek20.hla.generation.impl.core.context

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator

class ImplContextGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return module.name.value + "Impl"
    }

    override fun generateFileContent(): FileContent {
        return contentBuilder("impl.vm")
            .put("interfaces", module.interfaces)
            .build()
    }
}

class ContextGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "context"
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