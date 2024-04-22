package pl.bratek20.hla.generation.impl.core.language

import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.domain.MoreLanguageTypes
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator

abstract class LanguageStrategy(
    protected val c: ModuleGenerationContext
){
    abstract fun name(): ModuleLanguage

    abstract fun moduleDirName(): String
    abstract fun apiGenerator(): ApiGenerator

    abstract fun fixturesDirName(): String
    abstract fun buildersGenerator(): BuildersGenerator
    abstract fun assertsGenerator(): AssertsGenerator

    open fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()

    abstract fun types(): LanguageTypes
    abstract fun moreTypes(): MoreLanguageTypes
}