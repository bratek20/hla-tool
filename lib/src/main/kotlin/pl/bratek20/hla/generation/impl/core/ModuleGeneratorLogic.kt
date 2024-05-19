package pl.bratek20.hla.generation.impl.core

import pl.bratek20.architecture.properties.api.Properties
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.facade.api.HlaProperties
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.facade.api.ModuleName
import pl.bratek20.hla.facade.api.PROPERTIES_KEY
import pl.bratek20.hla.generation.api.GenerateArgs
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.context.ContextGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.impl.ImplGenerator
import pl.bratek20.hla.generation.impl.core.language.LanguageSupport
import pl.bratek20.hla.generation.impl.core.web.WebGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import pl.bratek20.hla.velocity.api.VelocityFacade

data class DomainContext(
    val modules: HlaModules,
    val properties: HlaProperties,
) {
    val module: ModuleDefinition
        get() = modules.current
}

private fun moduleDirectoryName(moduleName: ModuleName, languageSupport: LanguageSupport): String {
    return languageSupport.moduleNameToDirectoryName(moduleName.value)
}

class ModuleGeneratorLogic(
    private val velocity: VelocityFacade,
    private val properties: Properties,
) : ModuleGenerator {

    class MainDirectoryGenerator: DirectoryGenerator() {
        override fun getDirectoryName(): String {
            return moduleDirectoryName(module.name, c.language)
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
        override fun getDirectoryName(): String {
            return moduleDirectoryName(module.name, c.language)
        }

        override fun getDirectoryGenerators(): List<DirectoryGenerator> {
            return listOf(
                FixturesGenerator()
            )
        }
    }

    class GenerationRoot: DirectoryGenerator() {
        override fun getDirectoryName(): String {
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
        val language = args.language
        val modules = args.modules

        val hlaProperties = properties.get(
            InMemoryPropertiesSource.name,
            PROPERTIES_KEY,
            HlaProperties::class.java,
        )

        val domainContext = DomainContext(
            modules = HlaModules(moduleName, modules),
            properties = hlaProperties,
        )

        val context = ModuleGenerationContext(
            domain = domainContext,
            velocity = velocity,
            language = when (language) {
                ModuleLanguage.KOTLIN -> KotlinSupport(domainContext)
                ModuleLanguage.TYPE_SCRIPT -> TypeScriptSupport(domainContext)
            },
            onlyUpdate = args.onlyUpdate,
        )

        val root = GenerationRoot()
        root.init(context, "")

        val result = root.generateDirectory()
        requireNotNull(result)

        return GenerateResult(
            main = result.directories[0],
            testFixtures = result.directories[1]
        )
    }
}