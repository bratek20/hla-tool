package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.utils.camelToPascalCase

fun ofBaseType(value: String): BaseType {
    return BaseType.valueOf(value.uppercase())
}

fun isBaseType(value: String): Boolean {
    return BaseType.entries.any { it.name == value.uppercase() }
}



fun TypeDefinition.asNonWrappedWorldTypeName(): WorldTypeName {
    return WorldTypeName(this.getName())
}


fun FieldDefinition.asClassField(world: TypesWorldApi): WorldClassField {
    val name = this.getAttributes().find { attribute -> attribute.getName() == "from" }?.getValue() ?: this.getName()
    return WorldClassField.create(
        name,
        world.getTypeByName(this.getType().asWorldTypeName())
    )
}

fun createTypeDefinition(name: String): TypeDefinition {
    return TypeDefinition.create(name, emptyList())
}

fun createFieldDefinition(fieldName: String, typeName: String): FieldDefinition {
    return FieldDefinition.create(
        name = fieldName,
        type = createTypeDefinition(typeName),
        attributes = emptyList(),
        defaultValue = null
    )
}

fun ModuleGroup.getAllPropertyKeys(): List<KeyDefinition> {
    return this.getModules().flatMap { it.getPropertyKeys() }
}

fun methodsArgsTypeName(interf: InterfaceDefinition, method: MethodDefinition): String {
    return "${interf.getName()}${camelToPascalCase(method.getName())}Args"
}

open class BaseModuleGroupQueries(
    val group: ModuleGroup
) {

    private fun getModulesRecursive(group: ModuleGroup): List<ModuleDefinition> {
        val groupModules = group.getModules()
        val resolvedModules = group.getDependencies().flatMap {
            getModulesRecursive(it)
        }
        return groupModules + resolvedModules
    }
    val modules: List<ModuleDefinition>
        get() = getModulesRecursive(group)

    fun get(moduleName: ModuleName): ModuleDefinition {
        return modules.firstOrNull { it.getName() == moduleName } ?: throw IllegalStateException("Module $moduleName not found")
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

    fun findEvent(type: TypeDefinition): ComplexStructureDefinition? {
        return modules.firstNotNullOfOrNull { findEvent(type, it) }
    }

    private fun findEvent(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.getEvents().find { it.getName() == type.getName() }
    }

    private fun findEnum(type: TypeDefinition, module: ModuleDefinition): EnumDefinition? {
        return module.getEnums().find { it.getName() == type.getName() }
    }

    private fun findSimpleValueObject(type: TypeDefinition, module: ModuleDefinition): SimpleStructureDefinition? {
        return module.getSimpleValueObjects().find { it.getName() == type.getName() }
    }

    fun getComplexValueObjects(module: ModuleDefinition): List<ComplexStructureDefinition> {
        return module.getComplexValueObjects() + defsForMockedInterfaceArgs(module)
    }

    private fun findComplexValueObject(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return getComplexValueObjects(module).find { it.getName() == type.getName() }
    }

    private fun findDataClass(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return (module.getDataClasses() + (module.getImplSubmodule()?.getDataClasses() ?: emptyList())).find { it.getName() == type.getName() }
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

    fun findExternalType(type: TypeDefinition): String? {
        return modules.firstNotNullOfOrNull { findExternalType(type, it) }
    }

    private fun findExternalType(type: TypeDefinition, module: ModuleDefinition): String? {
        return module.getExternalTypes().find { it == type.getName() }
    }


    fun getGroup(module: ModuleName): ModuleGroup {
        return resolveAllGroups(group).first { it.getModules().any { it.getName() == module } }
    }

    private fun resolveAllGroups(group: ModuleGroup): List<ModuleGroup> {
        val groups = mutableListOf(group)
        group.getDependencies().forEach {
            groups.addAll(resolveAllGroups(it))
        }
        return groups
    }

    fun findTypeModuleName(typeName: String): ModuleName? {
        allTypeNames().forEach { (moduleName, typeNames) ->
            if (typeNames.contains(typeName)) {
                return moduleName
            }
        }
        return null
    }

    fun getTypeModuleName(typeName: String): ModuleName {
        return findTypeModuleName(typeName) ?:
        throw IllegalStateException("Type $typeName not found in any module")
    }

    fun findTypeModule(typeName: String): ModuleDefinition? {
        return findTypeModuleName(typeName)?.let { get(it) }
    }

    private fun allModuleTypeNames(module: ModuleDefinition): List<String> {
        return module.getEnums().map { it.getName() } +
                allSimpleStructureDefinitions(module).map { it.getName() } +
                allComplexStructureDefinitions(module).map { it.getName() } +
                module.getInterfaces().map { it.getName() } +
                module.getExternalTypes()
    }

    protected fun interfacesTypeNames(module: ModuleDefinition): List<String> {
        return module.getInterfaces().flatMap {
            it.getMethods().flatMap {
                    method -> method.getArgs().map {
                    arg -> arg.getType().getName()
            } +
                    method.getReturnType().getName()
            }
        }
    }

    data class Structures(
        val simple: List<SimpleStructureDefinition>,
        val complex: List<ComplexStructureDefinition>
    ) {
        fun areAllEmpty(): Boolean {
            return simple.isEmpty() && complex.isEmpty()
        }
    }
    fun allStructureDefinitions(module: ModuleDefinition): Structures {
        return Structures(
            simple = allSimpleStructureDefinitions(module),
            complex = allComplexStructureDefinitions(module)
        )
    }

    fun allSimpleStructureDefinitions(module: ModuleDefinition): List<SimpleStructureDefinition> {
        return module.getSimpleValueObjects() + module.getSimpleCustomTypes()
    }

    fun allComplexStructureDefinitions(module: ModuleDefinition): List<ComplexStructureDefinition> {
        var defs = getComplexValueObjects(module) +
                module.getComplexCustomTypes() +
                module.getDataClasses() +
                module.getEvents()
        val profile = getGroup(module.getName()).getProfile()

        if(profile.getSkipPatterns().contains(PatternName.Events) ||
            (profile.getOnlyPatterns().isNotEmpty() && !profile.getOnlyPatterns().contains(PatternName.Events))
        ) {
            val eventStructuresNamesToNotGenerate =  module.getEvents().map { it.getName() }
            defs = defs.filter { !eventStructuresNamesToNotGenerate.contains(it.getName()) }
        }
        return defs
    }

    private fun defsForMockedInterfaceArgs(module: ModuleDefinition): List<ComplexStructureDefinition> {
        return getMockedInterfaces(module).flatMap { interf ->
            interf.getMethods().mapNotNull { method ->
                if (method.getArgs().size > 1) {
                    val argsName = methodsArgsTypeName(interf, method)
                    methodArgsToStructure(argsName, method.getArgs())
                }
                else null
            }
        }
    }

    private fun methodArgsToStructure(name: String, args: List<ArgumentDefinition>): ComplexStructureDefinition {
        return ComplexStructureDefinition.create(
            name = name,
            fields = args.map { arg ->
                FieldDefinition.create(
                    name = arg.getName(),
                    type = arg.getType(),
                    attributes = emptyList(),
                    defaultValue = null
                )
            },
        )
    }

    private fun allTypeNames(): List<Pair<ModuleName, List<String>>> {
        return modules.map { it.getName() to allModuleTypeNames(it) }
    }

    protected fun hasType(module: ModuleDefinition, typeName: String): Boolean {
        return allModuleTypeNames(module).contains(typeName)
    }

    fun allEnumTypeDefinitions(module: ModuleDefinition): List<TypeDefinition> {
        return module.getEnums().map {
            TypeDefinition.create(
                name = it.getName(),
                wrappers = emptyList()
            )
        }
    }

    fun allExternalTypesDefinitions(module: ModuleDefinition): List<TypeDefinition> {
        return module.getExternalTypes().map {
            TypeDefinition.create(
                name = it,
                wrappers = emptyList()
            )
        }
    }

    fun getMockedInterfaces(module: ModuleDefinition): List<InterfaceDefinition> {
        return (module.getFixturesSubmodule()?.getMockedInterfaces() ?: emptyList())
            .map { module.getInterfaces().first { i -> i.getName() == it } }
    }
}

class ModuleGroupQueries(
    private val currentModuleName: ModuleName,
    group: ModuleGroup
): BaseModuleGroupQueries(group) {
    val currentModule: ModuleDefinition
        get() = get(currentModuleName)

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

    fun allExceptionNamesForCurrent(): List<String> {
        val interfaceExceptions = currentModule.getInterfaces()
            .flatMap { it.getMethods() }
            .flatMap { it.getThrows() }
            .map { it.getName() }
            .distinct()

        val extraExceptions = currentModule.getExceptions().map { it.getName() }
        val exceptionsToExclude = group.getModules().flatMap{ module -> module.getExceptions().map{it.getName()}}
        val notDefinedInterfaceExceptions = interfaceExceptions.filter { !exceptionsToExclude.contains(it) && !extraExceptions.contains(it) }

        return notDefinedInterfaceExceptions + extraExceptions
    }
}