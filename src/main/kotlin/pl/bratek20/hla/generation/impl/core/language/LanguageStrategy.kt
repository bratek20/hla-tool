package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension

interface LanguageStrategy {
    fun name(): ModuleLanguage

    fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()

    fun types(): LanguageTypes
    fun moreTypes(): MoreLanguageTypes

    fun structure(): LanguageStructure
}