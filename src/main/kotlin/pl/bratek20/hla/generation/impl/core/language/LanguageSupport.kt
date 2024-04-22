package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension

interface LanguageSupport {
    fun name(): ModuleLanguage

    fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()

    fun types(): LanguageTypes

    fun structure(): LanguageStructure

    fun assertsFixture(): LanguageAssertsFixture
    fun buildersFixture(): LanguageBuildersFixture
}