package pl.bratek20.hla.definitions.impl

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.facade.api.ModuleName

fun ofBaseType(value: String): BaseType {
    return BaseType.valueOf(value.uppercase())
}

fun isBaseType(value: String): Boolean {
    return BaseType.entries.any { it.name == value.uppercase() }
}

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

    fun findEnum(type: TypeDefinition): EnumDefinition? {
        return modules.firstNotNullOfOrNull { findEnum(type, it) }
    }

    private fun findEnum(type: TypeDefinition, module: ModuleDefinition): EnumDefinition? {
        return module.enums.find { it.name == type.name }
    }

    private fun findSimpleVO(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        val x = module.complexValueObjects.find { it.name == type.name }
        if (x == null) {
            return module.propertyValueObjects.find { it.name == type.name }
        }
        return x
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
}