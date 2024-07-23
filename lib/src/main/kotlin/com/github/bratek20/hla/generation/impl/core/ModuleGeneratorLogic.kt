package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
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
import com.github.bratek20.utils.directory.api.Directory

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

        val root = GenerationRoot()
        root.init(context, "")
        root.generateMacros()
        val result = root.generateDirectory()
        requireNotNull(result)

        val main = result.getDirectories()[0]
        val fixtures = result.getDirectories()[1]
        val tests = if (result.getDirectories().size > 2) result.getDirectories()[2] else null

        return GeneratedModule.create(
            name = moduleName,
            submodules = listOfNotNull(
                extractPatterns(
                    main, SubmoduleName.Api, listOf(
                        PatternName.Enums,
                        PatternName.ValueObjects,
                        PatternName.DataClasses,
                        PatternName.DataKeys,
                        PatternName.PropertyKeys,
                        PatternName.Interfaces,
                        PatternName.Exceptions,
                        PatternName.CustomTypes,
                        PatternName.CustomTypesMapper,
                        PatternName.SerializedCustomTypes,
                    )
                ),
                extractPatterns(
                    main, SubmoduleName.Impl, listOf(
                        PatternName.Logic,
                        PatternName.Data,
                    )
                ),
                extractPatterns(
                    main, SubmoduleName.Web, listOf(
                        PatternName.WebCommon,
                        PatternName.WebServer,
                        PatternName.WebClient,
                    )
                ),
                extractPatterns(
                    fixtures, SubmoduleName.Fixtures, listOf(
                        PatternName.Builders,
                        PatternName.Diffs,
                        PatternName.Asserts,
                    )
                ),
                extractPatterns(
                    tests, SubmoduleName.Tests, listOf(
                        PatternName.ImplTest,
                    )
                ),
            )
        )
    }

    private fun extractPatterns(dir: Directory?, submodule: SubmoduleName, patterns: List<PatternName>): GeneratedSubmodule? {
        if (dir == null) {
            return null
        }

        val foundPatterns = mutableListOf<GeneratedPattern>()
        val files = dir.getFiles()
        for (file in files) {
            val name = file.getName().value
            for (pattern in patterns) {
                if (name.lowercase() == pattern.name.lowercase()) {
                    foundPatterns.add(GeneratedPattern.create(pattern, file.getContent()))
                }
            }
        }
        return GeneratedSubmodule.create(submodule, foundPatterns)
    }
}