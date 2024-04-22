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
import pl.bratek20.hla.generation.impl.languages.kotlin.*
import pl.bratek20.hla.generation.impl.languages.typescript.*
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<HlaModule>): Directory {

        val context = ModuleGenerationContext(
            modules = HlaModules(moduleName, modules),
            velocity = velocity,
        )

        val stg = when (language) {
            ModuleLanguage.KOTLIN -> KotlinSupport(context)
            ModuleLanguage.TYPE_SCRIPT -> TypeScriptSupport(context)
        }

        context.language = stg


        val apiCode = ApiGenerator(context).generateDirectory()
        val fixturesCode = FixturesGenerator(context).generateDirectory()

        val x = Directory(
            name = context.language.structure().moduleDirName(),
            directories = listOf(
                apiCode,
                fixturesCode
            )
        )
        if (moduleName.value == "OtherModule" && language == ModuleLanguage.TYPE_SCRIPT) {
            DirectoryLogic().deleteDirectory(Path("tmp"))
            DirectoryLogic().writeDirectory(Path("tmp"), x)
        }
        return x
    }
}