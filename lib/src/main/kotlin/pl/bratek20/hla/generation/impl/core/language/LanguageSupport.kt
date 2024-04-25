package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension

interface LanguageSupport {
    fun name(): ModuleLanguage

    fun types(): LanguageTypes

    fun structure(): LanguageStructure

    fun assertsFixture(): LanguageAssertsPattern
    fun buildersFixture(): LanguageBuildersPattern
    fun dtoPattern(): LanguageDtoPattern

    fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()
}