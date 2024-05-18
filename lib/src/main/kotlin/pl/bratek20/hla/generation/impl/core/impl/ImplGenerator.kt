package pl.bratek20.hla.generation.impl.core.impl

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator

class LogicGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Logic"
    }

    override fun generateFileContent(): FileContent {
        return FileContent(listOf("xxx"))
    }
}

class ImplGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "impl"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return module.interfaces.isNotEmpty()
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            LogicGenerator()
        )
    }
}