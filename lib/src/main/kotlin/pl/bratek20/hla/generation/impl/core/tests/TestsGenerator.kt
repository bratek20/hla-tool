package pl.bratek20.hla.generation.impl.core.tests

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator

class ApiTestGenerator: FileGenerator() {
    override fun name(): String {
        return "ApiTest"
    }

    override fun generateFileContent(): FileContent? {
        return contentBuilder("apiTest.vm")
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
            ApiTestGenerator(),
        )
    }
}