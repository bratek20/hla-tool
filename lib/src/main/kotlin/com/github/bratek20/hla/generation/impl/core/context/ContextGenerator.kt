package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory

class ImplContextGenerator: FileGenerator() {
    override fun name(): String {
        return "Impl"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent {
        val factory = InterfaceViewFactory(apiTypeFactory)
        return contentBuilder("impl.vm")
            .put("interfaces", factory.create(module.getInterfaces()))
            .build()
    }
}

class WebContextGenerator: FileGenerator() {
    override fun name(): String {
        return "Web"
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule() == null) {
            return null
        }

        return contentBuilder("web.vm")
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
        return module.getInterfaces().isNotEmpty()
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            ImplContextGenerator(),
            WebContextGenerator()
        )
    }
}