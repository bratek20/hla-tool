package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.definitions.ModuleDefinition

data class DomainContext(
    val modules: HlaModules,
    val typeFactory: DomainTypeFactory = DomainTypeFactory(modules),
) {
    val module: ModuleDefinition
        get() = modules.current
}