package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.domain.ModuleName
import pl.bratek20.hla.generation.impl.core.fixtures.FixturesGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinApiGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinAssertsGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinBuildersGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptApiGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptAssertsGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptBuildersGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

abstract class LanguageStrategy(
    protected val c: ModulePartGeneratorContext
){
    abstract fun moduleDirName(): String
    abstract fun apiGenerator(): ApiGenerator

    abstract fun fixturesDirName(): String
    abstract fun buildersGenerator(): BuildersGenerator
    abstract fun assertsGenerator(): AssertsGenerator
}

class KotlinStrategy(c: ModulePartGeneratorContext)
    : LanguageStrategy(c) {

    override fun moduleDirName(): String {
        return c.moduleName.value.lowercase()
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
}

class TypeScriptStrategy(c: ModulePartGeneratorContext)
    : LanguageStrategy(c) {

    override fun moduleDirName(): String {
        return c.moduleName.value
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

    override fun generateModule(moduleName: String, modules: List<HlaModule>, lang: ModuleLanguage): Directory {
        val context = ModulePartGeneratorContext(
            moduleName = ModuleName(moduleName),
            modules = HlaModules(modules),
            velocity = velocity
        )

        val stg = when (lang) {
            ModuleLanguage.KOTLIN -> KotlinStrategy(context)
            ModuleLanguage.TYPE_SCRIPT -> TypeScriptStrategy(context)
        }

        val apiCode = stg.apiGenerator().generateDirectory()
        val fixturesCode = FixturesGenerator(stg).generateDirectory()

        val x = Directory(
            name = stg.moduleDirName(),
            directories = listOf(
                apiCode,
                fixturesCode
            )
        )
        if (false) {
            DirectoryLogic().writeDirectory(Path("tmp"), x)
        }
        return x
    }
}