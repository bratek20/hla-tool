package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.queries.ModuleGroupQueries

fun handleReferencing(modules: ModuleGroupQueries, typeName: String, base: String, submodule: String?): String {
    val module = modules.getTypeModuleName(typeName);
    return if (module == modules.currentModule.getName()) {
        base
    } else {
        if (submodule == null) {
            return "${module.value}.$base"
        }
        return "${module.value}.$submodule.$base"
    }
}

fun addModuleNamePrefix(modules: ModuleGroupQueries, typeName: String, base: String): String {
    val module = modules.getTypeModuleName(typeName);
    return "${module.value}.$base"
}