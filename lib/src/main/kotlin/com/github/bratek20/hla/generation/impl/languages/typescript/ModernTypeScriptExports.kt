package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.queries.api.getAllComplexValueObjects
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.camelToScreamingSnakeCase
import com.github.bratek20.utils.pascalToCamelCase

/**
 * Names exported by generated TypeScript files that TypesWorld does not model:
 * functions, constants and the mutable Api object, plus type-like symbols living in
 * submodules TypesWorld never populates (Web, Impl, Fixtures, Tests, Menu).
 *
 * TypesWorld stays the source of truth for api types and is consulted first; this
 * registry only fills the gaps. It is deliberately generous - a name registered here
 * but never generated is harmless, because an import is only emitted for a name the
 * file actually mentions.
 *
 * Entries marked as qualified-only are reachable through an explicit `Module.X.y`
 * reference but never through a bare identifier. Builder and assert functions are the
 * reason this distinction exists: both are the camelCase form of a structure name, so
 * they collide with the field and parameter names generated for that same structure.
 */
class ModernTypeScriptExports(modules: List<ModuleDefinition>) {
    private val bare = mutableMapOf<String, MutableList<ModernTypeScriptFile>>()
    private val qualified = mutableMapOf<String, MutableList<ModernTypeScriptFile>>()

    init {
        modules.forEach { collect(it) }
    }

    fun findInModule(name: String, module: ModuleName): ModernTypeScriptFile? {
        return qualified[name]?.firstOrNull { it.module == module }
    }

    fun findInSubmodule(name: String, module: ModuleName, submodule: SubmoduleName): ModernTypeScriptFile? {
        return qualified[name]?.firstOrNull { it.module == module && it.submodule == submodule }
    }

    fun find(name: String, preferredModule: ModuleName): ModernTypeScriptFile? {
        val candidates = bare[name] ?: return null
        return candidates.firstOrNull { it.module == preferredModule } ?: candidates.firstOrNull()
    }

    private fun collect(module: ModuleDefinition) {
        val name = module.getName()

        fun add(symbol: String, submodule: SubmoduleName, pattern: PatternName, bareResolvable: Boolean = true) {
            val file = ModernTypeScriptFile(name, submodule, pattern)
            qualified.computeIfAbsent(symbol) { mutableListOf() }.add(file)
            if (bareResolvable) {
                bare.computeIfAbsent(symbol) { mutableListOf() }.add(file)
            }
        }

        val simpleStructures = module.getSimpleValueObjects().map { it.getName() } +
                module.getSimpleCustomTypes().map { it.getName() }
        val complexStructures = module.getAllComplexValueObjects().map { it.getName() } +
                module.getComplexCustomTypes().map { it.getName() } +
                module.getDataClasses().map { it.getName() } +
                module.getEvents().map { it.getName() }
        val interfaceNames = module.getInterfaces().map { it.getName() }

        collectApi(module, ::add, simpleStructures, complexStructures)
        collectImpl(module, ::add, interfaceNames)
        collectWeb(module, ::add)
        collectFixtures(::add, simpleStructures + complexStructures, module.getEnums().map { it.getName() }, interfaceNames)

        // No `test` here - modern tests import it from vitest, not from TestBase.
        // The context variable is qualified-only: `c` is also the conventional parameter
        // name for HandlerContext all over Logic, ImplContext, Menu and the web handlers.
        add(CONTEXT_VARIABLE, SubmoduleName.Tests, PatternName.TestBase, false)
        listOf("setup", "SetupArgs").forEach {
            add(it, SubmoduleName.Tests, PatternName.TestBase, true)
        }

        if (module.getMenuSubmodule() != null) {
            add("init${name.value}Menu", SubmoduleName.Menu, PatternName.Menu)
        }
    }

    private fun collectApi(
        module: ModuleDefinition,
        add: (String, SubmoduleName, PatternName, Boolean) -> Unit,
        simpleStructures: List<String>,
        complexStructures: List<String>,
    ) {
        module.getExceptions().forEach { add(it.getName(), SubmoduleName.Api, PatternName.Exceptions, true) }
        module.getInterfaces().forEach { add(it.getName(), SubmoduleName.Api, PatternName.Interfaces, true) }
        module.getEnums().forEach { add(it.getName(), SubmoduleName.Api, PatternName.Enums, true) }
        module.getEvents().forEach { add(it.getName(), SubmoduleName.Api, PatternName.Events, true) }
        module.getDataClasses().forEach { add(it.getName(), SubmoduleName.Api, PatternName.DataClasses, true) }

        (simpleStructures + complexStructures).forEach {
            add(it, SubmoduleName.Api, PatternName.ValueObjects, true)
        }

        module.getPropertyKeys().forEach {
            add(camelToScreamingSnakeCase(it.getName() + "PropertyKey"), SubmoduleName.Api, PatternName.PropertyKeys, true)
        }
        module.getDataKeys().forEach {
            add(camelToScreamingSnakeCase(it.getName() + "DataKey"), SubmoduleName.Api, PatternName.DataKeys, true)
        }

        val customTypes = module.getSimpleCustomTypes().map { it.getName() } +
                module.getComplexCustomTypes().map { it.getName() }
        customTypes.forEach {
            add("Serialized$it", SubmoduleName.Api, PatternName.SerializedCustomTypes, true)
            add("${pascalToCamelCase(it)}Create", SubmoduleName.Api, PatternName.CustomTypesMapper, false)
            add("${pascalToCamelCase(it)}GetValue", SubmoduleName.Api, PatternName.CustomTypesMapper, false)
        }
        module.getComplexCustomTypes().forEach { customType ->
            customType.getFields().forEach { field ->
                add(
                    "${pascalToCamelCase(customType.getName())}Get${camelToPascalCase(field.getName())}",
                    SubmoduleName.Api,
                    PatternName.CustomTypesMapper,
                    false
                )
            }
        }
    }

    private fun collectImpl(
        module: ModuleDefinition,
        add: (String, SubmoduleName, PatternName, Boolean) -> Unit,
        interfaceNames: List<String>,
    ) {
        interfaceNames.forEach { add("${it}Logic", SubmoduleName.Impl, PatternName.Logic, true) }
        if (interfaceNames.isNotEmpty()) {
            add(API_OBJECT, SubmoduleName.Impl, PatternName.ImplContext, true)
        }

        module.getImplSubmodule()?.getDataClasses()?.forEach {
            add(it.getName(), SubmoduleName.Impl, PatternName.DataClasses, true)
        }
        module.getImplSubmodule()?.getDataKeys()?.forEach {
            add(camelToScreamingSnakeCase(it.getName() + "DataKey"), SubmoduleName.Impl, PatternName.DataKeys, true)
        }

        module.getTrackingSubmodule()?.let { tracking ->
            (tracking.getDimensions() + tracking.getEvents()).forEach {
                add(camelToPascalCase(it.getName()), SubmoduleName.Impl, PatternName.Track, true)
            }
        }
    }

    private fun collectWeb(module: ModuleDefinition, add: (String, SubmoduleName, PatternName, Boolean) -> Unit) {
        val web = module.getWebSubmodule() ?: return
        add("${module.getName().value}WebClientConfig", SubmoduleName.Web, PatternName.WebCommon, true)

        module.getInterfaces().forEach { interf ->
            add("${interf.getName()}WebClient", SubmoduleName.Web, PatternName.WebClient, true)
            interf.getMethods().forEach { method ->
                val base = interf.getName() + camelToPascalCase(method.getName())
                add("${base}Request", SubmoduleName.Web, PatternName.WebCommon, true)
                add("${base}Response", SubmoduleName.Web, PatternName.WebCommon, true)
            }
        }

        if (web.getPlayFabHandlers() != null) {
            add("RegisterDebugHandlers", SubmoduleName.Web, PatternName.PlayFabHandlers, true)
        }
    }

    private fun collectFixtures(
        add: (String, SubmoduleName, PatternName, Boolean) -> Unit,
        structureNames: List<String>,
        enumNames: List<String>,
        interfaceNames: List<String>,
    ) {
        structureNames.forEach {
            add(pascalToCamelCase(it), SubmoduleName.Fixtures, PatternName.Builders, false)
            add(pascalToCamelCase(it), SubmoduleName.Fixtures, PatternName.Asserts, false)
            add("${it}Def", SubmoduleName.Fixtures, PatternName.Builders, true)
            add("diff$it", SubmoduleName.Fixtures, PatternName.Diffs, true)
            add("Expected$it", SubmoduleName.Fixtures, PatternName.Diffs, true)
        }
        enumNames.forEach { add("diff$it", SubmoduleName.Fixtures, PatternName.Diffs, true) }

        interfaceNames.forEach {
            add("${it}Mock", SubmoduleName.Fixtures, PatternName.Mocks, true)
            add("create${it}Mock", SubmoduleName.Fixtures, PatternName.Mocks, false)
            add("setup$it", SubmoduleName.Fixtures, PatternName.Mocks, false)
        }
    }

    companion object {
        const val API_OBJECT = "Api"

        // Must match TestBaseGenerator.CONTEXT_NAME
        private const val CONTEXT_VARIABLE = "c"
    }
}
