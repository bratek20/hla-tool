package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.GenerateArgs
import com.github.bratek20.hla.generation.api.GenerateResult
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiGenerator
import com.github.bratek20.hla.generation.impl.core.api.MacrosGenerator
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

    class RootMainGenerator: DirectoryGenerator() {
        override fun name(): String {
            return module.getName().value
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                MacrosGenerator(),
                ApiGenerator(),
                ImplGenerator(),
                WebGenerator(),
                ContextGenerator()
            )
        }
    }

    class RootFixturesGenerator: DirectoryGenerator() {
        override fun name(): String {
            return module.getName().value
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                FixturesGenerator(),
            )
        }
    }

    class RootTestsGenerator: DirectoryGenerator() {
        override fun name(): String {
            return module.getName().value
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                TestsGenerator()
            )
        }
    }

    class GenerationRoot: DirectoryGenerator() {
        override fun name(): String {
            return "root"
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                RootMainGenerator(),
                RootFixturesGenerator(),
                RootTestsGenerator()
            )
        }
    }

    override fun generate(args: GenerateArgs): GenerateResult {
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

        val root = GenerationRoot()
        root.init(context, "")

        val result = root.generateDirectory()
        requireNotNull(result)

        return GenerateResult(
            main = result.getDirectories()[0],
            fixtures = result.getDirectories()[1],
            tests = if (result.getDirectories().size > 2) result.getDirectories()[2] else null,
        )
    }
}