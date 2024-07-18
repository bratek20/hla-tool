package com.github.bratek20.hla.generation.impl.languages.kotlin

import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.CodeBuilderLanguage
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.language.*

class KotlinSupport(private val c: DomainContext)
    : LanguageSupport {

    override fun name(): ModuleLanguage {
        return ModuleLanguage.KOTLIN
    }

    override fun types(): LanguageTypes {
        return KotlinTypes()
    }

    override fun filesExtension(): String {
        return "kt"
    }

    override fun adjustDirectoryName(directoryName: String): String {
        return directoryName.lowercase()
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        return KotlinAssertsPattern()
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        return KotlinBuildersPattern(types())
    }

    override fun dtoPattern(): LanguageDtoPattern {
        return KotlinDtoPattern()
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(PackageNameAndImportsExtension(c))
    }

    override fun base(): CodeBuilderLanguage {
        return Kotlin()
    }
}