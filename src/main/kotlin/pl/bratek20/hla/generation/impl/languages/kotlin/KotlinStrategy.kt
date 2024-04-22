package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.generation.impl.core.language.MoreLanguageTypes
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.generation.impl.core.language.LanguageStrategy
import pl.bratek20.hla.generation.impl.core.language.LanguageStructure

class KotlinStructure(private val c: ModuleGenerationContext) : LanguageStructure {
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
}

class KotlinStrategy(private val c: ModuleGenerationContext)
    : LanguageStrategy {

    override fun name(): ModuleLanguage {
        return ModuleLanguage.KOTLIN
    }

    override fun types(): LanguageTypes {
        return KotlinTypes()
    }

    override fun moreTypes(): MoreLanguageTypes {
        return KotlinMoreTypes(c.modules)
    }

    override fun structure(): LanguageStructure {
        return KotlinStructure(c)
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(PackageNameExtension(c))
    }
}