package pl.bratek20.hla.generation.impl.core.web

import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.web.dto.DtosGenerator

class WebGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "web"
    }

    override fun velocityDirPath(): String {
        return "web"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return c.domain.properties.generateWeb
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            DtosGenerator()
        )
    }
}