package com.github.bratek20.hla.generation.impl.languages.csharp

import com.github.bratek20.codebuilder.core.CodeBuilderLanguage
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.language.*
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptTypes

class CSharpSupport(private val c: DomainContext): LanguageSupport {
    override fun name(): ModuleLanguage {
        return ModuleLanguage.C_SHARP
    }

    override fun types(): LanguageTypes {
        return TypeScriptTypes(c.queries)
    }

    override fun filesExtension(): String {
        return "cs"
    }

    override fun adjustDirectoryName(directoryName: String): String {
        return directoryName
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        TODO("Not yet implemented")
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        TODO("Not yet implemented")
    }

    override fun dtoPattern(): LanguageDtoPattern {
        TODO("Not yet implemented")
    }

    override fun base(): CodeBuilderLanguage {
        TODO("Not yet implemented")
    }
}