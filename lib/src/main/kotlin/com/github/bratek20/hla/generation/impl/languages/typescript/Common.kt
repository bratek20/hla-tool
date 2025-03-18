package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.queries.api.ModuleGroupQueries

fun handleReferencing(modules: ModuleGroupQueries, typeName: String, base: String, submodule: String?): String {
    return addPrefixIfFromOtherModule(modules, typeName, base) {
        if (submodule == null) {
            "${it.value}."
        } else {
            "${it.value}.$submodule."
        }
    }
}

fun handleReferencingAlways(modules: ModuleGroupQueries, typeName: String, base: String, submodule: String?): String {
    return addPrefix(modules, typeName, base) {
        if (submodule == null) {
            "${it.value}."
        } else {
            "${it.value}.$submodule."
        }
    }
}

fun addModuleNamePrefix(modules: ModuleGroupQueries, typeName: String, base: String): String {
    val module = modules.getTypeModuleName(typeName);
    return "${module.value}.$base"
}

fun addPrefixIfFromOtherModule(
    modules: ModuleGroupQueries,
    typeName: String,
    base: String,
    prefixCalculator: (moduleName: ModuleName) -> String
): String {
    val module = modules.getTypeModuleName(typeName);
    return if (module == modules.currentModule.getName()) {
        base
    } else {
        "${prefixCalculator(module)}$base"
    }
}

fun addPrefix(
    modules: ModuleGroupQueries,
    typeName: String,
    base: String,
    prefixCalculator: (moduleName: ModuleName) -> String
): String {
    val module = modules.getTypeModuleName(typeName);
    return "${prefixCalculator(module)}$base"
}