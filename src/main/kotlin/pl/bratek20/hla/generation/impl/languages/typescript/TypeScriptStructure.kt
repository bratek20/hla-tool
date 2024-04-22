package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.language.LanguageStructure

class TypeScriptStructure(private val c: ModuleGenerationContext)
    : LanguageStructure
{
    override fun moduleDirName(): String {
        return c.module.name.value
    }

    override fun apiDirName(): String {
        return "Api"
    }

    override fun valueObjectsFileName(): String {
        return "ValueObjects.ts"
    }

    override fun interfacesFileName(): String {
        return "Interfaces.ts"
    }

    override fun fixturesDirName(): String {
        return "Fixtures"
    }

    override fun buildersFileName(): String {
        return "Builders.ts"
    }

    override fun assertsFileName(): String {
        return "Asserts.ts"
    }
}