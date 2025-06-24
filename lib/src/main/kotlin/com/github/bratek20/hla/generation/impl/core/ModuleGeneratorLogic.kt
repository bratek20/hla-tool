package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
import com.github.bratek20.hla.generation.impl.core.api.ApiGenerator
import com.github.bratek20.hla.generation.impl.core.context.ContextGenerator
import com.github.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import com.github.bratek20.hla.generation.impl.core.impl.ExamplesGenerator
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
import com.github.bratek20.hla.importscalculation.api.ImportsCalculator
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.velocity.api.VelocityFacade

data class DomainContext(
    val queries: ModuleGroupQueries,
    val profile: HlaProfile,
) {
    val module: ModuleDefinition
        get() = queries.currentModule
    val moduleGroup: ModuleGroup
        get() = queries.group
}

class ModuleGeneratorLogic(
    private val velocity: VelocityFacade,
    private val apiGenerator: ApiGenerator,
    private val viewModelGenerator: ViewModelGenerator,
    private val viewGenerator: ViewGenerator,
    private val prefabsGenerator: PrefabsGenerator,
    private val typesWorldApi: TypesWorldApi,
    private val hlaTypesWorldApi: HlaTypesWorldApi,
    private val patternGenerators: Set<PatternGenerator>,
    private val importsCalculator: ImportsCalculator
) : ModuleGenerator {
    class SubmodulesGenerator(
        private val context: ModuleGenerationContext,
        private val apiGenerator: ApiGenerator,
        private val viewModelGenerator: ViewModelGenerator,
        private val viewGenerator: ViewGenerator,
        private val prefabsGenerator: PrefabsGenerator,
        private val typesWorldApi: TypesWorldApi
    ) {
        fun generate(): List<GeneratedSubmodule> {
            return listOf(
                apiGenerator,
                ImplGenerator(),
                WebGenerator(),
                viewModelGenerator,
                viewGenerator,
                prefabsGenerator,
                ContextGenerator(),
                FixturesGenerator(),
                TestsGenerator(),
                ExamplesGenerator()
            ).mapNotNull {
                it.legacyInit(context, "", typesWorldApi)
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
            typesWorldApi = typesWorldApi,
        )

        (hlaTypesWorldApi as HlaTypesWorldApiLogic).init(context.apiTypeFactory)
        hlaTypesWorldApi.populate(args.getGroup())

        patternGenerators.forEach {
            it.init(importsCalculator)
        }

        return GeneratedModule.create(
            name = moduleName,
            submodules = SubmodulesGenerator(
                context,
                apiGenerator,
                viewModelGenerator,
                viewGenerator,
                prefabsGenerator,
                typesWorldApi
            ).generate()
        )
    }
}