package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class WebGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Web"
    }

    override fun velocityDirPath(): String {
        return "web"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return false
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
        )
    }
}