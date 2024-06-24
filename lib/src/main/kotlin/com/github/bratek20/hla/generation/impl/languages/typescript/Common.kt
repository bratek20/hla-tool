package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.definitions.impl.HlaModules

fun handleReferencing(modules: HlaModules, typeName: String, base: String, submodule: String?): String {
    val module = modules.getTypeModuleName(typeName);
    return if (module == modules.current.getName()) {
        base
    } else {
        if (submodule == null) {
            return "${module.value}.$base"
        }
        return "${module.value}.$submodule.$base"
    }
}

fun addModuleNamePrefix(modules: HlaModules, typeName: String, base: String): String {
    val module = modules.getTypeModuleName(typeName);
    return "${module.value}.$base"
}