package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.definitions.impl.HlaModules

fun handleReferencing(modules: HlaModules, typeName: String, base: String, submodule: String): String {
    val module = modules.getTypeModule(typeName);
    return if (module == modules.current.name) {
        base
    } else {
        return "${module.value}.$submodule.$base"
    }
}

fun addModuleNamePrefix(modules: HlaModules, typeName: String, base: String): String {
    val module = modules.getTypeModule(typeName);
    return "${module.value}.$base"
}