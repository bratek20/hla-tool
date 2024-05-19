package pl.bratek20.hla.generation.impl.core.impl

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.api.InterfaceViewFactory

class LogicGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Logic"
    }

    override fun generateFileContent(): FileContent {
        val factory = InterfaceViewFactory(apiTypeFactory)

        return contentBuilder("logic.vm")
            .put("interfaces", module.interfaces.map { factory.create(it) })
            .build()
    }
}

class ImplGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "impl"
    }

    override fun velocityDirPath(): String {
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