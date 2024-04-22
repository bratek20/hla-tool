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
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.generation.impl.core.language.LanguageStrategy
import pl.bratek20.hla.generation.impl.languages.kotlin.*
import pl.bratek20.hla.generation.impl.languages.typescript.*
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class KotlinStrategy(c: ModuleGenerationContext)
    : LanguageStrategy(c)
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.KOTLIN
    }

    override fun moduleDirName(): String {
        return c.module.name.value.lowercase()
    }

    override fun apiGenerator(): ApiGenerator {
        return KotlinApiGenerator(c)
    }

    override fun fixturesDirName(): String {
        return "fixtures"
    }

    override fun buildersGenerator(): BuildersGenerator {
        return KotlinBuildersGenerator(c)
    }

    override fun assertsGenerator(): AssertsGenerator {
        return KotlinAssertsGenerator(c)
    }

    override fun contentBuilderExtensions(): List<ContentBuilderExtension> {
        return listOf(PackageNameExtension(c))
    }

    override fun types(): LanguageTypes {
        return KotlinTypes()
    }

    override fun moreTypes(): MoreLanguageTypes {
        return KotlinMoreTypes(c.modules)
    }
}

class TypeScriptStrategy(c: ModuleGenerationContext)
    : LanguageStrategy(c)
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.TYPE_SCRIPT
    }

    override fun moduleDirName(): String {
        return c.module.name.value
    }

    override fun apiGenerator(): ApiGenerator {
        return TypeScriptApiGenerator(c)
    }

    override fun fixturesDirName(): String {
        return "Fixtures"
    }

    override fun buildersGenerator(): BuildersGenerator {
        return TypeScriptBuildersGenerator(c)
    }

    override fun assertsGenerator(): AssertsGenerator {
        return TypeScriptAssertsGenerator(c)
    }

    override fun types(): LanguageTypes {
        return TypeScriptTypes()
    }

    override fun moreTypes(): MoreLanguageTypes {
        return TypeScriptMoreTypes(c.modules)
    }
}

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<HlaModule>): Directory {

        val context = ModuleGenerationContext(
            modules = HlaModules(moduleName, modules),
            velocity = velocity,
        )

        val stg = when (language) {
            ModuleLanguage.KOTLIN -> KotlinStrategy(context)
            ModuleLanguage.TYPE_SCRIPT -> TypeScriptStrategy(context)
        }

        context.language = stg


        val apiCode = stg.apiGenerator().generateDirectory()
        val fixturesCode = FixturesGenerator(stg).generateDirectory()

        val x = Directory(
            name = stg.moduleDirName(),
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