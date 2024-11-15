package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.typesworld.api.*

fun ofBaseType(value: String): BaseType {
    return BaseType.valueOf(value.uppercase())
}

fun isBaseType(value: String): Boolean {
    return BaseType.entries.any { it.name == value.uppercase() }
}

class PrimitiveTypesPopulator: TypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    override fun populate(api: TypesWorldApi) {
        BaseType.entries.forEach {
            api.addPrimitiveType(
                HlaType.create(
                    name = HlaTypeName(it.name.lowercase()),
                    path = HlaTypePath.create(
                        GroupName("Language"),
                        ModuleName("Types"),
                        SubmoduleName.Api,
                        PatternName.Primitives
                    )
                )
            )
        }
    }
}

fun TypeDefinition.asHlaTypeName(): HlaTypeName {
    //TODO-FIX support for wrappers
    return HlaTypeName(this.getName())
}

fun FieldDefinition.asClassField(world: TypesWorldApi): ClassField {
    return ClassField.create(
        this.getName(),
        world.getTypeByName(this.getType().asHlaTypeName())
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

    fun ensureTypes() {
        getTypeNames().forEach { typeName ->
            val path = HlaTypePath.create(
                module.getName(),
                SubmoduleName.Api,
                getPatternName()
            )
        }
    }
    abstract fun addTypes()
}

class ComplexValueObjectsPopulator(
    private val defs: List<ComplexStructureDefinition>
): ApiPatternPopulator {
    override fun ensureTypes() {
        TODO("Not yet implemented")
    }

    override fun addTypes() {
        TODO("Not yet implemented")
    }

}

class ApiTypesPopulator(
    private val modules: List<ModuleDefinition>
): TypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    private lateinit var world: TypesWorldApi
    override fun populate(api: TypesWorldApi) {
        this.world = api
        modules.forEach(this::populateModuleTypes)
    }

    private fun populateModuleTypes(module: ModuleDefinition) {
        module.getSimpleValueObjects().forEach { populateSimpleValueObject(module, it) }
        module.getComplexValueObjects().forEach { populateComplexValueObject(module, it) }
    }

    private fun populateSimpleValueObject(
        module: ModuleDefinition,
        def: SimpleStructureDefinition
    ) {
        val type = HlaType.create(
            name = HlaTypeName(def.getName()),
            path = HlaTypePath.create(
                module.getName(),
                SubmoduleName.Api,
                PatternName.ValueObjects
            )
        )

        world.addClassType(ClassType.create(
            type = type,
            fields = listOf(
                createFieldDefinition("value", def.getTypeName())
                    .asClassField(world)
            )
        ))
    }
    private fun populateComplexValueObject(
        module: ModuleDefinition,
        def: ComplexStructureDefinition
    ) {
        val type = HlaType.create(
            name = HlaTypeName(def.getName()),
            path = HlaTypePath.create(
                module.getName(),
                SubmoduleName.Api,
                PatternName.ValueObjects
            )
        )

        world.addClassType(ClassType.create(
            type = type,
            fields = def.getFields().map { it.asClassField(world) }
        ))
    }


}

class ModuleGroupQueries(
    private val currentModuleName: ModuleName,
    private val group: ModuleGroup
) {
    fun populateTypes(typesWorldApi: TypesWorldApi) {
        typesWorldApi.populate(PrimitiveTypesPopulator())
        typesWorldApi.populate(ApiTypesPopulator(getModulesRecursive(group)))
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