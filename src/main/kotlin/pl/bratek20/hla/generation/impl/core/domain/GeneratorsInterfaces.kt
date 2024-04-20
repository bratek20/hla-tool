package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.asserts.AssertsGenerator
import pl.bratek20.hla.generation.impl.core.fixtures.builders.BuildersGenerator
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

abstract class LanguageStrategy(
    protected val c: ModuleGenerationContext
){
    abstract fun name(): ModuleLanguage

    abstract fun moduleDirName(): String
    abstract fun apiGenerator(): ApiGenerator

    abstract fun fixturesDirName(): String
    abstract fun buildersGenerator(): BuildersGenerator
    abstract fun assertsGenerator(): AssertsGenerator

    open fun contentBuilderExtensions(): List<ContentBuilderExtension> = emptyList()
}

class ModuleGenerationContext(
    val name: ModuleName,
    val modules: HlaModules,
    val velocity: VelocityFacade,
) {
    lateinit var language: LanguageStrategy
}

abstract class ContentBuilderExtension(
    protected val c: ModuleGenerationContext
) {
    abstract fun extend(builder: VelocityFileContentBuilder)
}

abstract class ModulePartGenerator(
    private val c: ModuleGenerationContext
) {
    protected val moduleName
        get() = c.name

    protected val module
        get() = c.modules.get(c.name)

    protected val modules
        get() = c.modules

    protected val velocity
        get() = c.velocity

    protected fun contentBuilder(fileName: String): VelocityFileContentBuilder {
        val path = "templates/${c.language.name().name.lowercase()}/$fileName"

        val builder = velocity.contentBuilder(path)
            .put("moduleName", moduleName.value)

        c.language.contentBuilderExtensions().forEach { it.extend(builder) }

        return builder
    }
}

abstract class ModulePartFileGenerator(c: ModuleGenerationContext)
    : ModulePartGenerator(c)
{
    abstract fun generateFile(): File
}

abstract class ModulePartDirectoryGenerator(c: ModuleGenerationContext)
    : ModulePartGenerator(c)
{
    abstract fun generateDirectory(): Directory
}