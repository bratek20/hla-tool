package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.model.HlaModule

data class DomainContext(
    val modules: HlaModules,
    val typeFactory: DomainTypeFactory = DomainTypeFactory(modules),
) {
    val module: HlaModule
        get() = modules.current
}