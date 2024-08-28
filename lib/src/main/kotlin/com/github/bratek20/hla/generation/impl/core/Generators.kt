package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.languages.csharp.cSharpFile
import com.github.bratek20.codebuilder.languages.kotlin.kotlinFile
import com.github.bratek20.codebuilder.languages.typescript.typeScriptFile
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.GeneratedPattern
import com.github.bratek20.hla.generation.api.GeneratedSubmodule
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.api.MacrosBuilder
import com.github.bratek20.hla.generation.impl.core.language.LanguageSupport
import com.github.bratek20.hla.generation.impl.languages.kotlin.profileToRootPackage
import com.github.bratek20.hla.velocity.api.TemplateNotFoundException
import com.github.bratek20.hla.velocity.api.VelocityFacade
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent

class ModuleGenerationContext(
    val domain: DomainContext,
    val velocity: VelocityFacade,
    val language: LanguageSupport,
    val onlyUpdate: Boolean,
    val onlyPatterns: List<String>
) {
    val module: ModuleDefinition
        get() = domain.module

    val apiTypeFactory: ApiTypeFactory
        get() = ApiTypeFactory(domain.queries, language.types())
}

interface ContentBuilderExtension{
    fun extend(builder: VelocityFileContentBuilder)
}

enum class GeneratorMode {
    START_AND_UPDATE,
    ONLY_START,
}

abstract class ModulePartGenerator {
    lateinit var c: ModuleGenerationContext
    lateinit var apiTypeFactory: ApiTypeFactory
    lateinit var velocityPath: String

    open fun velocityPathOverride(): String? {
        return null
    }

    open fun init(c: ModuleGenerationContext, velocityPath: String) {
        this.c = c
        this.apiTypeFactory = ApiTypeFactory(c.domain.queries, c.language.types())
        this.velocityPath = velocityPath
    }

    protected val module
        get() = c.module

    protected val modules
        get() = c.domain.queries

    protected val language
        get() = c.language

    protected val lang
        get() = c.language.base()

    protected fun contentBuilder(fileName: String): VelocityFileContentBuilder {
        val velocityPath = velocityPathOverride() ?: this.velocityPath
        val path = "templates/${c.language.name().name.lowercase()}/${velocityPath}/$fileName"

        val builder = c.velocity.contentBuilder(path)
            .put("moduleName", module.getName().value)

        c.language.contentBuilderExtensions().forEach { it.extend(builder) }

        return builder
    }
}

private fun submodulePackage(submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return profileToRootPackage(c.domain.profile) + "." + c.module.getName().value.lowercase() + "." + submodule.name.lowercase()
}

private fun submoduleNamespace(submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return c.module.getName().value + "." + submodule.name
}

abstract class PatternGenerator
    : ModulePartGenerator()
{
    lateinit var submodule: SubmoduleName


    @Deprecated("Migrate to code builder", ReplaceWith("applyOperations"))
    open fun generateFileContent(): FileContent? { return null }

    abstract fun patternName(): PatternName

    open fun mode(): GeneratorMode {
        return GeneratorMode.START_AND_UPDATE
    }

    open fun supportsCodeBuilder(): Boolean {
        return false
    }

    open fun shouldGenerate(): Boolean {
        return true
    }

    open fun getOperations(): TopLevelCodeBuilderOps = {}

    open fun extraKotlinImports(): List<String> {
        return emptyList()
    }

    open fun extraCSharpUsings(): List<String> {
        return emptyList()
    }

    open fun doNotGenerateTypeScriptNamespace(): Boolean {
        return false
    }

    private fun populatedCodeBuilder(): CodeBuilder {
        val cb = CodeBuilder(c.language.base())
        when (c.language.name()) {
            ModuleLanguage.KOTLIN -> {
                cb.kotlinFile {
                    packageName = submodulePackage(submodule, c)

                    extraKotlinImports().forEach {
                        addImport(it)
                    }

                    apply(getOperations())
                }
            }
            ModuleLanguage.TYPE_SCRIPT -> {
                cb.typeScriptFile {
                    if (doNotGenerateTypeScriptNamespace()) {
                        apply(getOperations())
                    }
                    else {
                        namespace {
                            name = submoduleNamespace(submodule, c)

                            apply(getOperations())
                        }
                    }
                }
            }
            ModuleLanguage.C_SHARP -> {
                cb.cSharpFile {
                    addUsing("B20.Ext")

                    extraCSharpUsings().forEach {
                        addUsing(it)
                    }

                    modules.getCurrentDependencies().forEach {
                        addUsing(it.getModule().getName().value + ".Api")
                    }

                    namespace(submoduleNamespace(submodule, c))

                    apply(getOperations())
                }
            }
        }
        return cb
    }

    fun generatePattern(): GeneratedPattern? {
        var content: FileContent?
        if (supportsCodeBuilder()) {
            if (shouldGenerate()) {
                val cb = populatedCodeBuilder()
                content = FileContent.fromString(cb.build())
            } else {
                content = null
            }
        }
        else {
            try {
                content = generateFileContent()
            } catch (e: TemplateNotFoundException) {
                //Hack: workaround to not add missing templates as I migrate out of velocity
                content = null
            }
        }

        if (content == null) {
            return null
        }

        if (mode() == GeneratorMode.START_AND_UPDATE) {
            val lines = listOf(
                "// DO NOT EDIT! Autogenerated by HLA tool",
                ""
            ) + content.lines
            content = FileContent(lines)
        }
        return GeneratedPattern.create(
            name = patternName(),
            file = File(
                name = patternName().name + "." + language.filesExtension(),
                content = content.toString()
            )
        )
    }

    fun shouldSkip(): Boolean {
        if(c.onlyUpdate && mode() == GeneratorMode.ONLY_START) {
            return true
        }

        if(c.onlyPatterns.isNotEmpty() && !c.onlyPatterns.contains(patternName().name)) {
            return true
        }
        return false
    }
}

abstract class SubmoduleGenerator
    : ModulePartGenerator()
{
    private lateinit var patternGenerators: List<PatternGenerator>

    abstract fun submoduleName(): SubmoduleName

    override fun init(c: ModuleGenerationContext, velocityPath: String) {
        super.init(c, velocityPath)

        patternGenerators = getPatternGenerators()

        patternGenerators.forEach {
            it.init(c, velocityDirPath())
            it.submodule = submoduleName()
        }
    }

    open fun velocityDirPath(): String {
        return ""
    }

    open fun shouldGenerateSubmodule(): Boolean {
        return true
    }

    abstract fun getPatternGenerators(): List<PatternGenerator>

    fun generateSubmodule(): GeneratedSubmodule? {
        if (!shouldGenerateSubmodule()) {
            return null
        }

        val patterns = mutableListOf<GeneratedPattern>()
        patternGenerators.forEach { patternGenerator ->
            if (patternGenerator.shouldSkip()) {
                return@forEach
            }
            patternGenerator.generatePattern()?.let { patterns.add(it) }
        }

        if (patterns.isEmpty()) {
            return null
        }

        return GeneratedSubmodule.create(
            name = submoduleName(),
            patterns = patterns
        )
    }

    //TODO-REF workaround to force macros generation
    fun generateMacros() {
        val macros = MacrosBuilder()
        macros.init(c, "macros")
        macros.generatePattern()
    }
}