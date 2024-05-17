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

    fun findPropertyVO(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findPropertyVO(type, it) }
    }

    fun findEnum(type: TypeDefinition): EnumDefinition? {
        return modules.firstNotNullOfOrNull { findEnum(type, it) }
    }

    fun findSimpleCustomType(type: TypeDefinition): SimpleStructureDefinition? {
        return modules.firstNotNullOfOrNull { findSimpleCustomType(type, it) }
    }

    fun findComplexCustomType(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findComplexCustomType(type, it) }
    }

    private fun findEnum(type: TypeDefinition, module: ModuleDefinition): EnumDefinition? {
        return module.enums.find { it.name == type.name }
    }

    private fun findSimpleVO(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.complexValueObjects.find { it.name == type.name }
    }

    private fun findPropertyVO(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.propertyValueObjects.find { it.name == type.name }
    }

    private fun findSimpleCustomType(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.simpleCustomTypes.find { it.name == type.name }
    }

    private fun findComplexCustomType(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.complexCustomTypes.find { it.name == type.name }
    }

    fun getCurrentDependencies(): List<ModuleName> {
        val typeNames = allComplexStructureDefinitions(current)
            .map { it.fields }
            .flatten()
            .map { it.type.name }

        return modules
            .filter { it.name != currentName }
            .filter { module ->
                typeNames.any { typeName ->
                    hasType(module, typeName)
                }
            }
            .map { it.name }
    }

    fun getTypeModule(typeName: String): ModuleName {
        allTypeNames().forEach { (moduleName, typeNames) ->
            if (typeNames.contains(typeName)) {
                return moduleName
            }
        }

        throw IllegalStateException("Type $typeName not found in any module")
    }

    private fun allModuleTypeNames(module: ModuleDefinition): List<String> {
        return module.enums.map { it.name } +
                module.simpleValueObjects.map { it.name } +
                module.complexValueObjects.map { it.name } +
                module.simpleCustomTypes.map { it.name } +
                module.complexCustomTypes.map { it.name } +
                module.propertyValueObjects.map { it.name }
    }

    private fun allComplexStructureDefinitions(module: ModuleDefinition): List<ComplexStructureDefinition> {
        return module.complexValueObjects + module.complexCustomTypes + module.propertyValueObjects
    }

    private fun allTypeNames(): List<Pair<ModuleName, List<String>>> {
        return modules.map { it.name to allModuleTypeNames(it) }
    }

    private fun hasType(module: ModuleDefinition, typeName: String): Boolean {
        return allModuleTypeNames(module).contains(typeName)
    }
}