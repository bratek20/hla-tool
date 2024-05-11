package pl.bratek20.hla.generation.impl.core

import pl.bratek20.architecture.properties.api.Properties
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.web.WebGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.*
import pl.bratek20.hla.generation.impl.languages.typescript.*
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.api.ModuleName
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.facade.api.HLA_PROPERTIES_KEY
import pl.bratek20.hla.facade.api.HlaProperties
import pl.bratek20.hla.generation.api.GenerateResult
import pl.bratek20.hla.velocity.api.VelocityFacade

class ModuleGeneratorLogic(
    private val velocity: VelocityFacade,
    private val properties: Properties,
) : ModuleGenerator {

    override fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<ModuleDefinition>): GenerateResult {
        val hlaProperties = properties.get(
            InMemoryPropertiesSource.name,
            HLA_PROPERTIES_KEY,
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
            }
        )

        val apiSubmodule = ApiGenerator(context).generateDirectory()
        val webSubmodule = WebGenerator(context).generateDirectory()
        val main = Directory(
            name = context.language.structure().moduleDirName(),
            directories = listOf(
                apiSubmodule,
                webSubmodule
            )
        )

        val fixturesDirectory = FixturesGenerator(context).generateDirectory()
        val testFixtures = Directory(
            name = context.language.structure().moduleDirName(),
            directories = listOf(
                fixturesDirectory
            )
        )

        return GenerateResult(
            main = main,
            testFixtures = testFixtures
        )
    }
}