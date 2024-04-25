package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.api.ViewTypeFactory
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.generation.impl.core.language.LanguageSupport
import pl.bratek20.hla.definitions.ModuleDefinition
import pl.bratek20.hla.definitions.TypeDefinition
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class ModuleGenerationContext(
    val domain: DomainContext,
    val velocity: VelocityFacade,
    val language: LanguageSupport
) {
    val module: ModuleDefinition
        get() = domain.module
}

interface ContentBuilderExtension{
    fun extend(builder: VelocityFileContentBuilder)
}

abstract class ModulePartGenerator(
    private val c: ModuleGenerationContext,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(c.domain.modules, c.language.types())
) {
    protected val module
        get() = c.module

    protected val modules
        get() = c.domain.modules

    protected val language
        get() = c.language

    protected fun contentBuilder(fileName: String): VelocityFileContentBuilder {
        val path = "templates/${c.language.name().name.lowercase()}/$fileName"

        val builder = c.velocity.contentBuilder(path)
            .put("moduleName", module.name.value)

        c.language.contentBuilderExtensions().forEach { it.extend(builder) }

        return builder
    }

    protected fun viewType(type: TypeDefinition?) = viewTypeFactory.create(type)
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