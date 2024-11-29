package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
import com.github.bratek20.hla.generation.impl.core.api.ApiGenerator
import com.github.bratek20.hla.generation.impl.core.context.ContextGenerator
import com.github.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import com.github.bratek20.hla.generation.impl.core.impl.ImplGenerator
import com.github.bratek20.hla.generation.impl.core.prefabs.PrefabsGenerator
import com.github.bratek20.hla.generation.impl.core.tests.TestsGenerator
import com.github.bratek20.hla.generation.impl.core.view.ViewGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ViewModelGenerator
import com.github.bratek20.hla.generation.impl.core.web.WebGenerator
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpSupport
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.impl.HlaTypesWorldApiLogic
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
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
    private val prefabsGenerator: PrefabsGenerator,
    private val typesWorldApi: TypesWorldApi,
    private val hlaTypesWorldApi: HlaTypesWorldApi
) : ModuleGenerator {
    class SubmodulesGenerator(
        private val context: ModuleGenerationContext,
        private val prefabsGenerator: PrefabsGenerator,
        private val typesWorldApi: TypesWorldApi
    ) {
        fun generate(): List<GeneratedSubmodule> {
            return listOf(
                ApiGenerator(),
                ImplGenerator(),
                WebGenerator(),
                ViewModelGenerator(),
                ViewGenerator(),
                prefabsGenerator,
                ContextGenerator(),
                FixturesGenerator(),
                TestsGenerator(),
            ).mapNotNull {
                it.init(context, "", typesWorldApi)
                it.generateMacros()
                it.generateSubmodule()
            }
        }
    }

    override fun generate(args: GenerateArgs): GeneratedModule {
        val moduleName = args.getModuleToGenerate()
        val profile = args.getGroup().getProfile()
        val language = profile.getLanguage()

        val queries = ModuleGroupQueries(moduleName, args.getGroup())

        val domainContext = DomainContext(
            queries = queries,
            profile = profile,
        )

        val context = ModuleGenerationContext(
            domain = domainContext,
            velocity = velocity,
            language = when (language) {
                ModuleLanguage.KOTLIN -> KotlinSupport(domainContext)
                ModuleLanguage.TYPE_SCRIPT -> TypeScriptSupport(domainContext)
                ModuleLanguage.C_SHARP -> CSharpSupport(domainContext)
            },
            onlyUpdate = args.getOnlyUpdate(),
            onlyPatterns = profile.getOnlyPatterns(),
        )

        (hlaTypesWorldApi as HlaTypesWorldApiLogic).apiTypeFactory = context.apiTypeFactory
        hlaTypesWorldApi.populate(args.getGroup())

        return GeneratedModule.create(
            name = moduleName,
            submodules = SubmodulesGenerator(context, prefabsGenerator, typesWorldApi).generate()
        )
    }
}