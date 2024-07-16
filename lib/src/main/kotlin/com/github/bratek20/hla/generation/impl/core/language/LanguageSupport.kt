package com.github.bratek20.hla.generation.impl.core.language

import com.github.bratek20.codebuilder.core.CodeBuilderLanguage
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension

interface LanguageSupport {
    fun name(): ModuleLanguage

    fun types(): LanguageTypes

    fun filesExtension(): String
    fun adjustDirectoryName(directoryName: String): String

    fun assertsFixture(): LanguageAssertsPattern
    fun buildersFixture(): LanguageBuildersPattern
    fun dtoPattern(): LanguageDtoPattern

    fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()

    fun base(): CodeBuilderLanguage
}