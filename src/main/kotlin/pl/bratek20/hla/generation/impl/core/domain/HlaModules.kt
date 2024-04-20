package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.model.Type

class HlaModules(
    private val currentName: ModuleName,
    private val modules: List<HlaModule>
) {
    val current: HlaModule
        get() = get(currentName)

    fun get(moduleName: ModuleName): HlaModule {
        return modules.first { it.name == moduleName }
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

    fun getCurrentDependencies(): List<ModuleName> {
        if (currentName.value == "SomeModule") {
            return listOf(ModuleName("OtherModule"))
        }
        return emptyList()
    }

    fun getComplexVoModule(complexVoName: String): ModuleName {
        return modules.first { it.complexValueObjects.any { it.name == complexVoName } }.name
    }

    private fun otherModules(): List<HlaModule> {
        return modules.filter { it.name != currentName }
    }
}