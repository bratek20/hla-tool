package com.github.bratek20.hla.generation.impl.core

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.languages.csharp.cSharpFile
import com.github.bratek20.codebuilder.languages.kotlin.kotlinFile
import com.github.bratek20.codebuilder.languages.typescript.typeScriptFile
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.GeneratedPattern
import com.github.bratek20.hla.generation.api.GeneratedSubmodule
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.generation.impl.core.api.MacrosBuilder
import com.github.bratek20.hla.generation.impl.core.language.LanguageSupport
import com.github.bratek20.hla.generation.impl.languages.kotlin.profileToRootPackage
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.importscalculation.api.ImportsCalculator
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.velocity.api.TemplateNotFoundException
import com.github.bratek20.hla.velocity.api.VelocityFacade
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent

class ModuleGenerationContext(
    val domain: DomainContext,
    val velocity: VelocityFacade,
    val language: LanguageSupport,
    val onlyUpdate: Boolean,
    val onlyPatterns: List<String>,
    val typesWorldApi: TypesWorldApi
) {
    val module: ModuleDefinition
        get() = domain.module

    val apiTypeFactory: ApiTypeFactoryLogic
        get() = ApiTypeFactoryLogic(domain.queries, language.types(), typesWorldApi)
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
    lateinit var apiTypeFactory: ApiTypeFactoryLogic
    lateinit var velocityPath: String
    lateinit var typesWorldApi: TypesWorldApi

    open fun velocityPathOverride(): String? {
        return null
    }

    open fun legacyInit(c: ModuleGenerationContext, velocityPath: String, typesWorldApi: TypesWorldApi) {
        this.c = c
        this.apiTypeFactory = ApiTypeFactoryLogic(c.domain.queries, c.language.types(), typesWorldApi)
        this.velocityPath = velocityPath
        this.typesWorldApi = typesWorldApi
    }

    protected val module
        get() = c.module
    protected val moduleGroup
        get() = c.domain.queries.group

    protected val moduleName
        get() = module.getName().value

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

private fun submodulePackage(group: ModuleGroup, submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return submodulePackageForModule(group, c.module.getName(), submodule, c)
}

private fun submodulePackageForModule(group: ModuleGroup, moduleName: ModuleName, submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return profileToRootPackage(group.getProfile()) + "." + moduleName.value.lowercase() + "." + submodule.name.lowercase()
}

private fun submoduleNamespace(submodule: SubmoduleName, c: ModuleGenerationContext): String {
    return c.module.getName().value + "." + submodule.name
}

class PerFileOperations(
    val fileName: String,
    val ops: TopLevelCodeBuilderOps,
)
abstract class PatternGenerator
    : ModulePartGenerator()
{
    lateinit var submodule: SubmoduleName

    private lateinit var importsCalculator: ImportsCalculator
    fun init(
        importsCalculator: ImportsCalculator
    ) {
        this.importsCalculator = importsCalculator
    }

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

    open fun getOperations(): TopLevelCodeBuilderOps? = null
    open fun getOperationsPerFile(): List<PerFileOperations> = emptyList()
    open fun getFiles(): List<File> = emptyList()
    open fun getDirectory(): Directory? = null

    open fun extraKotlinImports(): List<String> {
        return emptyList()
    }

    open fun extraCSharpUsings(): List<String> {
        return emptyList()
    }

    open fun doNotGenerateTypeScriptNamespace(): Boolean {
        return false
    }

    open fun generateFiles(): List<File> {
        return emptyList()
    }

    open fun useImportsCalculator(): Boolean {
        return false
    }

    private fun generateFileContent(ops: TopLevelCodeBuilderOps): FileContent {
        val cb = CodeBuilder(c.language.base())
        when (c.language.name()) {
            ModuleLanguage.KOTLIN -> {
                cb.kotlinFile {
                    packageName = submodulePackage(c.domain.queries.group, submodule, c)

                    extraKotlinImports().forEach {
                        addImport(it)
                    }

                    modules.getCurrentDependencies().forEach { dep ->
                        val submodules = mutableListOf(
                            submodule
                        )
                        if (submodule == SubmoduleName.Fixtures) {
                            submodules.add(SubmoduleName.Api)
                        }
                        submodules.forEach {
                            addImport(submodulePackageForModule(dep.getGroup(), dep.getModule().getName(), it, c) + ".*")
                        }
                    }

                    apply(ops)
                }
            }
            ModuleLanguage.TYPE_SCRIPT -> {
                cb.typeScriptFile {
                    if (doNotGenerateTypeScriptNamespace()) {
                        apply(ops)
                    }
                    else {
                        namespace {
                            name = submoduleNamespace(submodule, c)

                            apply(ops)
                        }
                    }
                }
            }
            ModuleLanguage.C_SHARP -> {
                cb.cSharpFile {
                    addUsing("System")
                    addUsing("System.Collections.Generic")
                    addUsing("System.Linq")
                    addUsing("B20.Ext")

                    if (useImportsCalculator()) {
                        val usingsToAdd = extraCSharpUsings().toMutableSet()

                        val patternPath = HlaTypePath.create(
                            c.module.getName(),
                            submodule,
                            patternName()
                        )
                        importsCalculator.calculate(patternPath).forEach {
                            usingsToAdd.add(it)
                        }

                        usingsToAdd.forEach {
                            addUsing(it)
                        }
                    }
                    else {
                        extraCSharpUsings().forEach {
                            addUsing(it)
                        }

                        if (submodule != SubmoduleName.Api) {
                            addUsing("$moduleName.Api")
                        }
                        if (submodule == SubmoduleName.View) {
                            addUsing("$moduleName.ViewModel")
                        }
                        if (submodule == SubmoduleName.Context && patternName() == PatternName.WebClientContext) {
                            addUsing("$moduleName.Web")
                        }
                        if (submodule == SubmoduleName.Context && patternName() == PatternName.ViewModel) {
                            addUsing("$moduleName.ViewModel")
                        }
                        modules.getCurrentDependencies().forEach {
                            addUsing(it.getModule().getName().value + ".Api")
                            if (submodule == SubmoduleName.ViewModel && it.getModule().getViewModelSubmodule() != null) {
                                addUsing(it.getModule().getName().value + ".ViewModel")
                            }
                            if (submodule == SubmoduleName.View && it.getModule().getViewModelSubmodule() != null) {
                                addUsing(it.getModule().getName().value + ".ViewModel")
                                addUsing(it.getModule().getName().value + ".View")
                            }
                            if (submodule == SubmoduleName.Fixtures) {
                                addUsing(it.getModule().getName().value + ".Fixtures")
                            }
                        }
                    }

                    namespace(submoduleNamespace(submodule, c))

                    apply(ops)
                }
            }
        }
        return FileContent.fromString(cb.build())
    }

    fun generatePatterns(): List<GeneratedPattern> {
        val files = generateFiles()
        if (files.isNotEmpty()) {
            return files.map {
                GeneratedPattern.create(
                    name = patternName(),
                    file = it,
                    directory = null
                )
            }
        }

        // legacy support
        if (!supportsCodeBuilder()) {
            try {
                return generateFileContent()?.let {
                    listOf(generatePatternFile(it))
                } ?: emptyList()
            } catch (e: TemplateNotFoundException) {
                //Hack: workaround to not add missing templates as I migrate out of velocity
                return emptyList()
            }
        }

        if (!shouldGenerate()) {
            return emptyList()
        }

        val generatedPatterns: MutableList<GeneratedPattern> = mutableListOf()
        val content = getOperations()?.let { generateFileContent(it) }
        if (content != null) {
            generatedPatterns.add(generatePatternFile(content))
        }

        getOperationsPerFile().forEach {
            val c = generateFileContent(it.ops)
            generatedPatterns.add(generatePatternFile(c, it.fileName))
        }

        getFiles().forEach {
            generatedPatterns.add(GeneratedPattern.create(patternName(), it, null))
        }

        getDirectory()?.let {
            generatedPatterns.add(GeneratedPattern.create(patternName(), null, it))
        }

        return generatedPatterns
    }

    private fun generatePatternFile(initContent: FileContent, fileName: String = patternName().name): GeneratedPattern {
        var finalContent = initContent
        if (mode() == GeneratorMode.START_AND_UPDATE) {
            val lines = listOf(
                "// DO NOT EDIT! Autogenerated by HLA tool",
                ""
            ) + initContent.lines
            finalContent = FileContent(lines)
        }

        return GeneratedPattern.create(
            name = patternName(),
            file = File(
                name = fileName + "." + language.filesExtension(),
                content = finalContent.toString()
            ),
            directory = null
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

    override fun legacyInit(c: ModuleGenerationContext, velocityPath: String, typesWorldApi: TypesWorldApi) {
        super.legacyInit(c, velocityPath, typesWorldApi)

        patternGenerators = getPatternGenerators()

        patternGenerators.forEach {
            it.legacyInit(c, velocityDirPath(), typesWorldApi)
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
            patterns.addAll(patternGenerator.generatePatterns())
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
        macros.legacyInit(c, "macros", typesWorldApi)
        macros.generatePatterns()
    }
}