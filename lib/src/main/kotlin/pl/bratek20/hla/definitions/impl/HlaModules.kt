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
        return modules.first { it.getName() == moduleName }
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
        return module.getEnums().find { it.getName() == type.getName() }
    }

    private fun findSimpleValueObject(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.getSimpleValueObjects().find { it.getName() == type.getName() }
    }

    private fun findComplexValueObject(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.getComplexValueObjects().find { it.getName() == type.getName() }
    }

    private fun findDataClass(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return (module.getDataClasses() + module.getImplSubmodule().getDataClasses()).find { it.getName() == type.getName() }
    }

    private fun findSimpleCustomType(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.getSimpleCustomTypes().find { it.getName() == type.getName() }
    }

    private fun findComplexCustomType(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.getComplexCustomTypes().find { it.getName() == type.getName() }
    }

    fun getCurrentDependencies(): List<ModuleName> {
        val typeNames = allComplexStructureDefinitions(current)
            .map { it.getFields() }
            .flatten()
            .map { it.getType().getName() } +
            interfacesTypeNames(current)

        return modules
            .filter { it.getName() != currentName }
            .filter { module ->
                typeNames.any { typeName ->
                    hasType(module, typeName)
                }
            }
            .map { it.getName() }
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
        return module.getEnums().map { it.getName() } +
                allSimpleStructureDefinitions(module).map { it.getName() } +
                allComplexStructureDefinitions(module).map { it.getName() }
    }

    private fun interfacesTypeNames(module: ModuleDefinition): List<String> {
        return module.getInterfaces().flatMap {
            it.getMethods().flatMap {
                method -> method.getArgs().map {
                    arg -> arg.getType().getName()
                } +
                method.getReturnType().getName()
            }
        }
    }

    fun allSimpleStructureDefinitions(module: ModuleDefinition): List<SimpleStructureDefinition> {
        return module.getSimpleValueObjects() + module.getSimpleCustomTypes()
    }

    fun allComplexStructureDefinitions(module: ModuleDefinition): List<ComplexStructureDefinition> {
        return module.getComplexValueObjects() + module.getComplexCustomTypes() + module.getDataClasses()
    }

    private fun allTypeNames(): List<Pair<ModuleName, List<String>>> {
        return modules.map { it.getName() to allModuleTypeNames(it) }
    }

    private fun hasType(module: ModuleDefinition, typeName: String): Boolean {
        return allModuleTypeNames(module).contains(typeName)
    }
}