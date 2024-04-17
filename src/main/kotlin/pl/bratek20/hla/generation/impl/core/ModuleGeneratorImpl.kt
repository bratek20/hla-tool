package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinApiGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinFixturesGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptApiGenerator
import pl.bratek20.hla.generation.impl.languages.typescript.TypeScriptFixturesGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

abstract class LanguageStrategy(
    protected val module: HlaModule,
    protected val velocity: VelocityFacadeImpl
){
    abstract fun moduleDirName(): String
    abstract fun apiGenerator(): ApiGenerator
    abstract fun fixturesGenerator(): FixturesGenerator
}

class KotlinStrategy(module: HlaModule, velocity: VelocityFacadeImpl)
    : LanguageStrategy(module, velocity) {

    override fun moduleDirName(): String {
        return module.name.lowercase()
    }

    override fun apiGenerator(): ApiGenerator {
        return KotlinApiGenerator(module, velocity)
    }

    override fun fixturesGenerator(): FixturesGenerator {
        return KotlinFixturesGenerator(module, velocity)
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

    override fun fixturesGenerator(): FixturesGenerator {
        return TypeScriptFixturesGenerator(module, velocity)
    }
}

class ModuleGeneratorImpl : ModuleGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    override fun generateCode(module: HlaModule, lang: ModuleLanguage): Directory {
        val stg = when (lang) {
            ModuleLanguage.KOTLIN -> KotlinStrategy(module, velocity)
            ModuleLanguage.TYPE_SCRIPT -> TypeScriptStrategy(module, velocity)
        }

        val apiCode = stg.apiGenerator().generateCode()
        val fixturesCode = stg.fixturesGenerator().generateCode()

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