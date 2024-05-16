package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.definitions.impl.HlaModules

fun handleReferencing(modules: HlaModules, typeName: String, base: String, submodule: String? = null): String {
    val module = modules.getTypeModule(typeName);
    return if (module == modules.current.name) {
        base
    } else {
        if (submodule == null) {
            return "${module.value}.$base"
        }
        return "${module.value}.$submodule.$base"
    }
}