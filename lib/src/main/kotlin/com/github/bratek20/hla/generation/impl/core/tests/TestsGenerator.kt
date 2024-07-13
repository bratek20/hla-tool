package com.github.bratek20.hla.generation.impl.core.tests

import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode

class ImplTestGenerator: FileGenerator() {
    override fun name(): String {
        return "ImplTest"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent? {
        return contentBuilder("implTest.vm")
            .build()
    }
}

class TestsGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Tests"
    }

    override fun velocityDirPath(): String {
        return "tests"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            ImplTestGenerator(),
        )
    }
}