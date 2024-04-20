package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.domain.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.domain.LanguageStrategy
import pl.bratek20.hla.generation.impl.core.domain.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinApiGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinAssertsGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinBuildersGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.PackageNameExtension
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptApiGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptAssertsGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptBuildersGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class KotlinStrategy(c: ModuleGenerationContext)
    : LanguageStrategy(c)
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.KOTLIN
    }

    override fun moduleDirName(): String {
        return c.name.value.lowercase()
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
}

class TypeScriptStrategy(c: ModuleGenerationContext)
    : LanguageStrategy(c)
{
    override fun name(): ModuleLanguage {
        return ModuleLanguage.TYPE_SCRIPT
    }

    override fun moduleDirName(): String {
        return c.name.value
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
}

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generate(moduleName: ModuleName, language: ModuleLanguage, modules: List<HlaModule>): Directory {

        val context = ModuleGenerationContext(
            name = moduleName,
            modules = HlaModules(modules),
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
        if (false && moduleName.value == "SomeModule" && language == ModuleLanguage.KOTLIN) {
            DirectoryLogic().writeDirectory(Path("tmp"), x)
        }
        return x
    }
}