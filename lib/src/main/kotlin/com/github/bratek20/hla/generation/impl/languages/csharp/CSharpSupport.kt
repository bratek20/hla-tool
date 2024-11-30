package com.github.bratek20.hla.generation.impl.languages.csharp

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilderLanguage
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.language.*
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptAssertsPattern
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptBuildersPattern
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptDtoPattern
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptTypes

class CSharpSupport(private val c: DomainContext): LanguageSupport {
    override fun name(): ModuleLanguage {
        return ModuleLanguage.C_SHARP
    }

    override fun types(): LanguageTypes {
        return CSharpTypes()
    }

    override fun filesExtension(): String {
        return "cs"
    }

    override fun adjustDirectoryName(directoryName: String): String {
        return directoryName
    }

    override fun assertsFixture(): LanguageAssertsPattern {
        return TypeScriptAssertsPattern(c.queries)
    }

    override fun buildersFixture(): LanguageBuildersPattern {
        return CSharpBuildersPattern(c.queries)
    }

    override fun dtoPattern(): LanguageDtoPattern {
        return TypeScriptDtoPattern(c.queries)
    }

    override fun base(): CodeBuilderLanguage {
        return CSharp()
    }
}