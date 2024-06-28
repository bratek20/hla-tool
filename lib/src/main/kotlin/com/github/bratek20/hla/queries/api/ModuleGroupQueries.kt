package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.parsing.api.ModuleGroup

fun ofBaseType(value: String): BaseType {
    return BaseType.valueOf(value.uppercase())
}

fun isBaseType(value: String): Boolean {
    return BaseType.entries.any { it.name == value.uppercase() }
}

class ModuleGroupQueries(
    private val currentModuleName: ModuleName,
    private val group: ModuleGroup
) {
    val currentModule: ModuleDefinition
        get() = get(currentModuleName)

    private fun getModulesRecursive(group: ModuleGroup): List<ModuleDefinition> {
        val groupModules = group.getModules()
        val resolvedModules = group.getDependencies().flatMap {
            getModulesRecursive(it)
        }
        return groupModules + resolvedModules
    }
    private val modules: List<ModuleDefinition>
        get() = getModulesRecursive(group)

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

    fun findInterface(type: TypeDefinition): InterfaceDefinition? {
        return modules.firstNotNullOfOrNull { findInterface(type, it) }
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

    private fun findInterface(type: TypeDefinition, module: ModuleDefinition): InterfaceDefinition? {
        return module.getInterfaces().find { it.getName() == type.getName() }
    }

    fun getCurrentDependencies(): List<ModuleDependency> {
        val typeNames = allComplexStructureDefinitions(currentModule)
            .map { it.getFields() }
            .flatten()
            .map { it.getType().getName() } +
            interfacesTypeNames(currentModule)

        val resolvedModules = modules
            .filter { it.getName() != currentModuleName }
            .filter { module ->
                typeNames.any { typeName ->
                    hasType(module, typeName)
                }
            }
            .map { it.getName() }

        return resolvedModules.map { ModuleDependency.create(getGroup(it), get(it)) }
    }

    private fun getGroup(module: ModuleName): ModuleGroup {
        return resolveAllGroups(group).first { it.getModules().any { it.getName() == module } }
    }

    private fun resolveAllGroups(group: ModuleGroup): List<ModuleGroup> {
        val groups = mutableListOf(group)
        group.getDependencies().forEach {
            groups.addAll(resolveAllGroups(it))
        }
        return groups
    }

    fun getTypeModuleName(typeName: String): ModuleName {
        allTypeNames().forEach { (moduleName, typeNames) ->
            if (typeNames.contains(typeName)) {
                return moduleName
            }
        }

        throw IllegalStateException("Type $typeName not found in any module")
    }

    fun getTypeModule(typeName: String): ModuleDefinition {
        return get(getTypeModuleName(typeName))
    }

    private fun allModuleTypeNames(module: ModuleDefinition): List<String> {
        return module.getEnums().map { it.getName() } +
                allSimpleStructureDefinitions(module).map { it.getName() } +
                allComplexStructureDefinitions(module).map { it.getName() } +
                module.getInterfaces().map { it.getName() } +
                module.getExternalTypes()
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

    fun findExternalType(type: TypeDefinition): String? {
        return modules.firstNotNullOfOrNull { findExternalType(type, it) }
    }

    private fun findExternalType(type: TypeDefinition, module: ModuleDefinition): String? {
        return module.getExternalTypes().find { it == type.getName() }
    }
}