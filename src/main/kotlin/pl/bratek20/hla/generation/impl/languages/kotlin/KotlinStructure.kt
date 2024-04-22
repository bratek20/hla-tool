package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.generation.impl.core.language.LanguageStructure

class KotlinStructure(private val c: DomainContext) : LanguageStructure {
    override fun moduleDirName(): String {
        return c.module.name.value.lowercase()
    }

    override fun apiDirName(): String {
        return "api"
    }

    override fun valueObjectsFileName(): String {
        return "ValueObjects.kt"
    }

    override fun interfacesFileName(): String {
        return "Interfaces.kt"
    }

    override fun fixturesDirName(): String {
        return "fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.kt"
    }

    override fun assertsFileName(): String {
        return "Asserts.kt"
    }

    override fun webDirName(): String {
        return "web"
    }

    override fun dtosFileName(): String {
        return "Dtos.kt"
    }
}