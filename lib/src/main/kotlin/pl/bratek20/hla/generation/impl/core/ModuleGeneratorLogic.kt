package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.generation.api.GenerateArgs
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.context.ContextGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.impl.ImplGenerator
import pl.bratek20.hla.generation.impl.core.tests.TestsGenerator
import pl.bratek20.hla.generation.impl.core.web.WebGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import pl.bratek20.hla.velocity.api.VelocityFacade

data class DomainContext(
    val modules: HlaModules,
    val profile: HlaProfile,
) {
    val module: ModuleDefinition
        get() = modules.current
}

class ModuleGeneratorLogic(
    private val velocity: VelocityFacade,
) : ModuleGenerator {

    class MainDirectoryGenerator: DirectoryGenerator() {
        override fun name(): String {
            return module.name.value
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

    class TestFixturesGenerator: DirectoryGenerator() {
        override fun name(): String {
            return module.name.value
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                FixturesGenerator(),
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
                MainDirectoryGenerator(),
                TestFixturesGenerator()
            )
        }
    }

    override fun generate(args: GenerateArgs): GenerateResult {
        val moduleName = args.moduleName
        val language = args.profile.language
        val modules = args.modules

        val domainContext = DomainContext(
            modules = HlaModules(moduleName, modules),
            profile = args.profile,
        )

        val context = ModuleGenerationContext(
            domain = domainContext,
            velocity = velocity,
            language = when (language) {
                ModuleLanguage.KOTLIN -> KotlinSupport(domainContext)
                ModuleLanguage.TYPE_SCRIPT -> TypeScriptSupport(domainContext)
            },
            onlyUpdate = args.onlyUpdate,
            onlyParts = args.profile.onlyParts,
        )

        val root = GenerationRoot()
        root.init(context, "")

        val result = root.generateDirectory()
        requireNotNull(result)

        return GenerateResult(
            main = result.directories[0],
            fixtures = result.directories[1]
        )
    }
}