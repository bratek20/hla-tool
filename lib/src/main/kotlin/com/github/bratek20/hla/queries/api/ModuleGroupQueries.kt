package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.typesworld.api.*

fun ofBaseType(value: String): BaseType {
    return BaseType.valueOf(value.uppercase())
}

fun isBaseType(value: String): Boolean {
    return BaseType.entries.any { it.name == value.uppercase() }
}

class PrimitiveTypesPopulator {

    fun populate(api: TypesWorldApi) {
        BaseType.entries.forEach {
            api.addPrimitiveType(
                WorldType.create(
                    name = WorldTypeName(it.name.lowercase()),
                    path = HlaTypePath.create(
                        GroupName("Language"),
                        ModuleName("Types"),
                        SubmoduleName.Api,
                        PatternName.Primitives
                    ).asWorld()
                )
            )
        }
    }
}

fun TypeDefinition.asWorldTypeName(): WorldTypeName {
    //TODO-FIX support for wrappers
    return WorldTypeName(this.getName())
}

fun FieldDefinition.asClassField(world: TypesWorldApi): WorldClassField {
    return WorldClassField.create(
        this.getName(),
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

abstract class ApiPatternPopulator {
    private lateinit var module: ModuleDefinition
    protected lateinit var world: TypesWorldApi

    fun init(module: ModuleDefinition, world: TypesWorldApi) {
        this.module = module
        this.world = world
    }

    protected abstract fun getTypeNames(): List<String>
    protected abstract fun getPatternName(): PatternName

    fun ensurePatternTypes() {
        getTypeNames().forEach { typeName ->
            world.ensureType(getMyPatternType(typeName))
        }
    }

    protected fun getMyPatternType(typeName: String): WorldType {
        val path = HlaTypePath.create(
            module.getName(),
            SubmoduleName.Api,
            getPatternName()
        ).asWorld()
        return WorldType.create(
            name = WorldTypeName(typeName),
            path = path
        )
    }

    open fun addPatternTypes() {
        //no-op
    }
}

abstract class SimpleStructurePopulator(
    private val defs: List<SimpleStructureDefinition>
): ApiPatternPopulator() {
    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }

    override fun addPatternTypes() {
        defs.forEach { def ->
            world.addClassType(WorldClassType.create(
                type = getMyPatternType(def.getName()),
                fields = listOf(
                    createFieldDefinition("value", def.getTypeName())
                        .asClassField(world)
                )
            ))
        }
    }
}

class SimpleValueObjectsPopulator(
    defs: List<SimpleStructureDefinition>
): SimpleStructurePopulator(defs) {
    override fun getPatternName() = PatternName.ValueObjects
}

class SimpleCustomTypesPopulator(
    defs: List<SimpleStructureDefinition>
): SimpleStructurePopulator(defs) {
    override fun getPatternName() = PatternName.CustomTypes
}

abstract class ComplexStructuresPopulator(
    private val defs: List<ComplexStructureDefinition>
): ApiPatternPopulator() {
    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }

    override fun addPatternTypes() {
        defs.forEach { def ->
            world.addClassType(WorldClassType.create(
                type = getMyPatternType(def.getName()),
                fields = def.getFields().map { it.asClassField(world) }
            ))
        }
    }
}

class ComplexValueObjectsPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.ValueObjects
}

class ComplexCustomTypesPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.CustomTypes
}

class DataClassesPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.DataClasses
}

class EnumsPopulator(
    private val defs: List<EnumDefinition>
): ApiPatternPopulator() {
    override fun getPatternName() = PatternName.Enums

    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }
}

//TODO-FIX current abstraction is not suited for external types i.e they do not have pattern name
class ExternalTypesPopulator(
    private val defs: List<String>
): ApiPatternPopulator() {
    //TODO-FIX
    override fun getPatternName() = PatternName.ValueObjects

    override fun getTypeNames(): List<String> {
        return defs
    }
}

class ApiTypesPopulator(
    private val modules: List<ModuleDefinition>
) {
    private lateinit var world: TypesWorldApi

    fun populate(api: TypesWorldApi) {
        this.world = api
        val populators = modules.flatMap { createPatternPopulators(it) }

        populators.forEach { it.ensurePatternTypes() }
        populators.forEach { it.addPatternTypes() }
    }

    private fun createPatternPopulators(module: ModuleDefinition): List<ApiPatternPopulator> {
        val populators = listOf(
            SimpleValueObjectsPopulator(module.getSimpleValueObjects()),
            ComplexValueObjectsPopulator(module.getComplexValueObjects()),

            SimpleCustomTypesPopulator(module.getSimpleCustomTypes()),
            ComplexCustomTypesPopulator(module.getComplexCustomTypes()),

            DataClassesPopulator(module.getDataClasses()),

            EnumsPopulator(module.getEnums()),

            ExternalTypesPopulator(module.getExternalTypes())
        )

        populators.forEach { populator ->
            populator.init(module, world)
        }

        return populators
    }
}

class ModuleGroupQueries(
    private val currentModuleName: ModuleName,
    private val group: ModuleGroup
) {
    fun populateTypes(typesWorldApi: TypesWorldApi) {
        PrimitiveTypesPopulator().populate(typesWorldApi)
        ApiTypesPopulator(getModulesRecursive(group)).populate(typesWorldApi)
    }

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

    private fun findComplexValueObject(type: TypeDefinition, module: ModuleDefinition): ComplexStructureDefinition? {
        return module.getComplexValueObjects().find { it.getName() == type.getName() }
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

    fun getTypeModule(typeName: String): ModuleDefinition {
        return get(getTypeModuleName(typeName))
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
        return module.getComplexValueObjects() + module.getComplexCustomTypes() + module.getDataClasses() + module.getEvents()
    }

    fun allExceptionNamesForCurrent(): List<String> {
        val interfaceExceptions = currentModule.getInterfaces()
            .flatMap { it.getMethods() }
            .flatMap { it.getThrows() }
            .map { it.getName() }
            .distinct()

        val extraExceptions = currentModule.getExceptions().map { it.getName() }

        return interfaceExceptions + extraExceptions
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
}