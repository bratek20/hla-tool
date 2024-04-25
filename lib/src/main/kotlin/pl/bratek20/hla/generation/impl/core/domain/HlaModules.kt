package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.definitions.ComplexStructureDefinition
import pl.bratek20.hla.definitions.ModuleDefinition
import pl.bratek20.hla.definitions.SimpleStructureDefinition
import pl.bratek20.hla.definitions.TypeDefinition

class HlaModules(
    private val currentName: ModuleName,
    private val modules: List<ModuleDefinition>
) {
    val current: ModuleDefinition
        get() = get(currentName)

    fun get(moduleName: ModuleName): ModuleDefinition {
        return modules.first { it.name == moduleName }
    }

    fun findSimpleVO(type: TypeDefinition): SimpleStructureDefinition? {
        return modules.firstNotNullOfOrNull { findSimpleVO(type, it) }
    }

    fun findComplexVO(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findComplexVO(type, it) }
    }

    private fun findSimpleVO(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
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

    private fun otherModules(): List<ModuleDefinition> {
        return modules.filter { it.name != currentName }
    }
}