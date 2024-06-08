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

    fun findSimpleValueObject(type: TypeDefinition): SimpleStructureDefinition? {
        return modules.firstNotNullOfOrNull { findSimpleValueObject(type, it) }
    }

    fun findComplexValueObject(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findComplexValueObject(type, it) }
    }

    fun findDataClass(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findDataClass(type, it) }
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

    private fun findSimpleValueObject(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexValueObject(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.complexValueObjects.find { it.name == type.name }
    }

    private fun findDataClass(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return (module.dataClasses + module.implSubmodule.data).find { it.name == type.name }
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
            .map { it.type.name } +
            interfacesTypeNames(current)

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
                allSimpleStructureDefinitions(module).map { it.name } +
                allComplexStructureDefinitions(module).map { it.name }
    }

    private fun interfacesTypeNames(module: ModuleDefinition): List<String> {
        return module.interfaces.flatMap {
            it.methods.flatMap {
                method -> method.args.map {
                    arg -> arg.type.name
                } +
                method.returnType.name
            }
        }
    }

    fun allSimpleStructureDefinitions(module: ModuleDefinition): List<SimpleStructureDefinition> {
        return module.simpleValueObjects + module.simpleCustomTypes
    }

    fun allComplexStructureDefinitions(module: ModuleDefinition): List<ComplexStructureDefinition> {
        return module.complexValueObjects + module.complexCustomTypes + module.dataClasses
    }

    private fun allTypeNames(): List<Pair<ModuleName, List<String>>> {
        return modules.map { it.name to allModuleTypeNames(it) }
    }

    private fun hasType(module: ModuleDefinition, typeName: String): Boolean {
        return allModuleTypeNames(module).contains(typeName)
    }
}