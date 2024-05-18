package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.generation.impl.core.language.LanguageSupport
import pl.bratek20.hla.definitions.api.ModuleDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.FileContent
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

abstract class ModulePartGenerator {
    lateinit var c: ModuleGenerationContext
    lateinit var  apiTypeFactory: ApiTypeFactory

    open fun init(c: ModuleGenerationContext) {
        this.c = c
        this.apiTypeFactory = ApiTypeFactory(c.domain.modules, c.language.types())
    }

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

    protected fun apiType(type: TypeDefinition?) = apiTypeFactory.create(type)
}

abstract class FileGenerator
    : ModulePartGenerator()
{
    abstract fun getBaseFileName(): String
    abstract fun generateFileContent(): FileContent?

    fun generateFile(): File? {
        val content = generateFileContent() ?: return null
        return File(
            name = getBaseFileName() + "." + language.filesExtension(),
            content = content
        )
    }
}

abstract class DirectoryGenerator
    : ModulePartGenerator()
{
    abstract fun getDirectoryName(): String

    open fun shouldGenerateDirectory(): Boolean {
        return true
    }

    open fun getFileGenerators(): List<FileGenerator> {
        return emptyList()
    }

    open fun getDirectoryGenerators(): List<DirectoryGenerator> {
        return emptyList()
    }

    fun generateDirectory(): Directory? {
        if (!shouldGenerateDirectory()) {
            return null
        }

        val files = mutableListOf<File>()
        getFileGenerators().forEach { fileGenerator ->
            fileGenerator.init(c)
            fileGenerator.generateFile()?.let { files.add(it) }
        }

        val directories = mutableListOf<Directory>()
        getDirectoryGenerators().forEach { dirGenerator ->
            dirGenerator.init(c)
            dirGenerator.generateDirectory()?.let { directories.add(it) }
        }

        return Directory(
            name = language.adjustDirectoryName(getDirectoryName()),
            files = files,
            directories = directories
        )
    }
}