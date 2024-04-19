package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
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
    protected val module: HlaModule,
    protected val velocity: VelocityFacadeImpl
){
    abstract fun moduleDirName(): String
    abstract fun apiGenerator(): ApiGenerator

    abstract fun fixturesDirName(): String
    abstract fun buildersGenerator(): BuildersGenerator
    abstract fun assertsGenerator(): AssertsGenerator
}

class KotlinStrategy(module: HlaModule, velocity: VelocityFacadeImpl)
    : LanguageStrategy(module, velocity) {

    override fun moduleDirName(): String {
        return module.name.lowercase()
    }

    override fun apiGenerator(): ApiGenerator {
        return KotlinApiGenerator(module, velocity)
    }

    override fun fixturesDirName(): String {
        return "fixtures"
    }

    override fun buildersGenerator(): BuildersGenerator {
        return KotlinBuildersGenerator(module, velocity)
    }

    override fun assertsGenerator(): AssertsGenerator {
        return KotlinAssertsGenerator(module, velocity)
    }
}

class TypeScriptStrategy(module: HlaModule, velocity: VelocityFacadeImpl)
    : LanguageStrategy(module, velocity) {

    override fun moduleDirName(): String {
        return module.name
    }

    override fun apiGenerator(): ApiGenerator {
        return TypeScriptApiGenerator(module, velocity)
    }

    override fun fixturesDirName(): String {
        return "Fixtures"
    }

    override fun buildersGenerator(): BuildersGenerator {
        return TypeScriptBuildersGenerator(module, velocity)
    }

    override fun assertsGenerator(): AssertsGenerator {
        return TypeScriptAssertsGenerator(module, velocity)
    }
}

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generateCode(module: HlaModule, lang: ModuleLanguage): Directory {
        val stg = when (lang) {
            ModuleLanguage.KOTLIN -> KotlinStrategy(module, velocity)
            ModuleLanguage.TYPE_SCRIPT -> TypeScriptStrategy(module, velocity)
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
        //DirectoryLogic().writeDirectory(Path("tmp"), x)
        return x
    }
}