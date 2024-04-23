package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.web.WebGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.*
import pl.bratek20.hla.generation.impl.languages.typescript.*
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<HlaModule>): Directory {

        val domainContext = DomainContext(
            modules = HlaModules(moduleName, modules),
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
        val fixturesSubmodule = FixturesGenerator(context).generateDirectory()
        val webSubmodule = WebGenerator(context).generateDirectory()

        val x = Directory(
            name = context.language.structure().moduleDirName(),
            directories = listOf(
                apiSubmodule,
                fixturesSubmodule,
                webSubmodule
            )
        )
        if (moduleName.value == "SomeModule" && language == ModuleLanguage.KOTLIN) {
            DirectoryLogic().deleteDirectory(Path("tmp"))
            DirectoryLogic().writeDirectory(Path("tmp"), x)
        }
        return x
    }
}