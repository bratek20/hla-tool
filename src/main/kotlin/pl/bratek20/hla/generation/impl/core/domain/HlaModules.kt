package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.model.Type

class HlaModules(
    private val modules: List<HlaModule>
) {
    fun get(moduleName: ModuleName): HlaModule {
        return modules.first { it.name == moduleName.value }
    }

    fun findSimpleVO(type: Type): SimpleValueObject? {
        return modules.firstNotNullOfOrNull { findSimpleVO(type, it) }
    }

    fun findComplexVO(type: Type): ComplexValueObject? {
        return modules.firstNotNullOfOrNull { findComplexVO(type, it) }
    }

    private fun findSimpleVO(type: Type, module: HlaModule): SimpleValueObject? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: Type, module: HlaModule): ComplexValueObject? {
        return module.complexValueObjects.find { it.name == type.name }
    }
}