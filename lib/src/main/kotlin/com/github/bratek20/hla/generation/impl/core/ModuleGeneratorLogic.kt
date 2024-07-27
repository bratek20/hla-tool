package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
import com.github.bratek20.hla.generation.impl.core.api.ApiGenerator
import com.github.bratek20.hla.generation.impl.core.context.ContextGenerator
import com.github.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import com.github.bratek20.hla.generation.impl.core.impl.ImplGenerator
import com.github.bratek20.hla.generation.impl.core.tests.TestsGenerator
import com.github.bratek20.hla.generation.impl.core.web.WebGenerator
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import com.github.bratek20.hla.velocity.api.VelocityFacade

data class DomainContext(
    val queries: ModuleGroupQueries,
    val profile: HlaProfile,
) {
    val module: ModuleDefinition
        get() = queries.currentModule
}

class ModuleGeneratorLogic(
    private val velocity: VelocityFacade,
) : ModuleGenerator {
    class SubmodulesGenerator(
        private val context: ModuleGenerationContext,
    ) {
        fun generate(): List<GeneratedSubmodule> {
            return listOf(
                ApiGenerator(),
                ImplGenerator(),
                WebGenerator(),
                ContextGenerator(),
                FixturesGenerator(),
                TestsGenerator(),
            ).mapNotNull {
                it.init(context, "")
                it.generateMacros()
                it.generateSubmodule()
            }
        }
    }

    override fun generate(args: GenerateArgs): GeneratedModule {
        val moduleName = args.getModuleToGenerate()
        val profile = args.getGroup().getProfile()
        val language = profile.getLanguage()

        val domainContext = DomainContext(
            queries = ModuleGroupQueries(moduleName, args.getGroup()),
            profile = profile,
        )

        val context = ModuleGenerationContext(
            domain = domainContext,
            velocity = velocity,
            language = when (language) {
                ModuleLanguage.KOTLIN -> KotlinSupport(domainContext)
                ModuleLanguage.TYPE_SCRIPT -> TypeScriptSupport(domainContext)
            },
            onlyUpdate = args.getOnlyUpdate(),
            onlyPatterns = profile.getOnlyPatterns(),
        )

        return GeneratedModule.create(
            name = moduleName,
            submodules = SubmodulesGenerator(context).generate()
        )
    }
}