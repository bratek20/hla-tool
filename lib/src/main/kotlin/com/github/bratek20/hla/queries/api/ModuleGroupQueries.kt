package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.api.ComplexValueObjectApiType
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelTypesMapper
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.generation.impl.core.viewmodel.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asHla
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
            val path = HlaTypePath.create(
                GroupName("Language"),
                ModuleName("Types"),
                SubmoduleName.Api,
                PatternName.Primitives
            ).asWorld()

            api.addPrimitiveType(
                WorldType.create(
                    name = WorldTypeName(it.name.lowercase()),
                    path = path
                )
            )
            api.ensureType(
                WorldType.create(
                    name = WorldTypeName("List<${it.name.lowercase()}>"),
                    path = path
                )
            )
            api.ensureType(
                WorldType.create(
                    name = WorldTypeName("Optional<${it.name.lowercase()}>"),
                    path = path
                )
            )
        }
    }
}

class B20FrontendTypesPopulator {
    companion object{
        val emptyModelType = WorldType.create(
            name = WorldTypeName("EmptyModel"),
            path = HlaTypePath.create(
                listOf(
                    GroupName("B20"),
                    GroupName("Frontend")
                ),
                ModuleName("UiElements"),
                SubmoduleName.Api,
                PatternName.ValueObjects
            ).asWorld()
        )
    }
    fun populate(api: TypesWorldApi) {
        api.addClassType(WorldClassType.create(
            type = emptyModelType,
            fields = emptyList()
        ))
    }
}

fun TypeDefinition.asWorldTypeName(): WorldTypeName {
    if (this.getWrappers().contains(TypeWrapper.LIST)) {
        return WorldTypeName("List<${this.getName()}>")
    }
    if (this.getWrappers().contains(TypeWrapper.OPTIONAL)) {
        return WorldTypeName("Optional<${this.getName()}>")
    }
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

class ViewModelTypesPopulator(
    private val modules: List<ModuleDefinition>,
    private val apiTypeFactory: ApiTypeFactory
) {
    private lateinit var world: TypesWorldApi

    fun populate(api: TypesWorldApi) {
        this.world = api
        modules.forEach { populateElements(it) }
        modules.forEach { populateEnumSwitches(it) }
        modules.forEach { populateElementGroups(it) }
    }

    private fun populateElements(module: ModuleDefinition) {
        module.getViewModelSubmodule()?.let {
            it.getElements().forEach { element ->
                val path = HlaTypePath.create(
                    module.getName(),
                    SubmoduleName.ViewModel,
                    PatternName.GeneratedElements
                ).asWorld()

                val modelName = element.getModel()?.getName() ?: "EmptyModel"
                val paramType = WorldType.create(
                    WorldTypeName("UiElement<${modelName}>"),
                    path
                )
                world.addConcreteParametrizedClass(WorldConcreteParametrizedClass.create(
                    type = paramType,
                    typeArguments = listOf(
                        world.getTypeByName(WorldTypeName(modelName))
                    )
                ))
                world.addClassType(WorldClassType.create(
                    type = WorldType.create(
                        name = WorldTypeName(element.getName()),
                        path = path
                    ),
                    fields = getFieldsForElement(element),
                    extends = paramType
                ))
            }
        }
    }

    private fun populateEnumSwitches(module: ModuleDefinition) {
        val ensuredEnumSwitches = world.getAllTypes().filter {
            it.getName().value.endsWith("Switch") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        val ensuredEnumSwitchGroups = world.getAllTypes().filter {
            it.getName().value.endsWith("SwitchGroup") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        val extractedEnumSwitchesFromGroups = ensuredEnumSwitchGroups.map { group ->
            val enumSwitch = group.getName().value.removeSuffix("Group")
            WorldType.create(
                name = WorldTypeName(enumSwitch),
                path = group.getPath()
            )
        }

        val enumSwitches = ensuredEnumSwitches + extractedEnumSwitchesFromGroups

        enumSwitches.forEach { enumSwitch ->
            val enumType = world.getTypeByName(WorldTypeName(enumSwitch.getName().value.replace("Switch", "")))

            val paramType = WorldType.create(
                WorldTypeName("EnumSwitch<${enumType.getName()}>"),
                enumSwitch.getPath()
            )
            world.addConcreteParametrizedClass(WorldConcreteParametrizedClass.create(
                type = paramType,
                typeArguments = listOf(
                    enumType
                )
            ))

            world.addClassType(
                WorldClassType.create(
                    type = enumSwitch,
                    fields = emptyList(),
                    extends = paramType
                )
            )
        }
    }

    private fun populateElementGroups(module: ModuleDefinition) {
        val ensuredGroups = world.getAllTypes().filter {
            it.getName().value.endsWith("Group") &&
                    it.getPath().asHla().getModuleName() == module.getName()
        }

        ensuredGroups.forEach {
            val wrappedTypeName = it.getName().value.removeSuffix("Group")
            val modelType = getModelTypeForEnsuredUiElement(world, wrappedTypeName)
            val viewModelType = world.getTypeByName(WorldTypeName(wrappedTypeName))
            val paramType = WorldType.create(
                WorldTypeName("UiElementGroup<${viewModelType.getName()},${modelType.getName()}>"),
                it.getPath()
            )
            world.addConcreteParametrizedClass(WorldConcreteParametrizedClass.create(
                type = paramType,
                typeArguments = listOf(
                    viewModelType,
                    modelType
                )
            ))
            world.addClassType(
                WorldClassType.create(
                    type = it,
                    fields = emptyList(),
                    extends = paramType
                )
            )
        }
    }

    private fun getFieldsForElement(def: ViewModelElementDefinition): List<WorldClassField> {
        return def.getModel()?.let { model ->
            model.getMappedFields().map {
                val type = mapModelField(model.getName(), it)
                WorldClassField.create(it, type)
            }
        } ?: emptyList()
    }

    private fun mapModelField(modelTypeName: String, fieldName: String): WorldType {
        val modelType = apiTypeFactory.create(createTypeDefinition(modelTypeName)) as ComplexValueObjectApiType
        val field = modelType.fields.find { it.name == fieldName }
            ?: throw IllegalStateException("Field $fieldName not found in model $modelTypeName")
        return mapper.mapModelToViewModelType(field.type)
    }

    companion object {
        val mapper = BaseViewModelTypesMapper()
    }
}

class ViewTypesPopulator(
    private val modules: List<ModuleDefinition>,
) {
    private lateinit var world: TypesWorldApi
    private lateinit var mapper: ModelToViewModelTypeMapper

    fun populate(api: TypesWorldApi, apiTypeFactory: ApiTypeFactory) {
        world = api
        mapper = ModelToViewModelTypeMapper(apiTypeFactory, api)

        modules.forEach { populate(it) }
    }

    private fun populate(module: ModuleDefinition) {
        val viewModelTypes = world.getAllTypes().filter {
            it.getPath().asHla().getSubmoduleName() == SubmoduleName.ViewModel &&
                it.getPath().asHla().getModuleName() == module.getName() &&
                    !it.getName().value.contains("<")
        }

        viewModelTypes.forEach { type ->
            val classType = world.getClassType(type)
            val viewClassType = mapper.mapViewModelToViewType(classType.getType())
            val viewFields = classType.getFields().map { field ->
                WorldClassField.create(
                    field.getName(),
                    mapper.mapViewModelToViewType(field.getType())
                )
            }

            world.addClassType(WorldClassType.create(
                type = viewClassType,
                fields = viewFields
            ))
        }
    }

//    override fun populateType(typesWorldApi: TypesWorldApi) {
//        typesWorldApi.addClassType(WorldClassType.create(
//            type = getViewClassType(),
//            getFields().filter {
//                it.worldType != null
//            }.map {
//                WorldClassField.create(
//                    it.name,
//                    mapper.mapViewModelToViewType(it.worldType!!)
//                )
//            }
//        ))
//    }

//    override fun populateType(typesWorldApi: TypesWorldApi) {
//        typesWorldApi.addConcreteWrapper(WorldConcreteWrapper.create(
//            WorldType.create(
//                name = WorldTypeName(getViewClassName()),
//                path = getViewClassType().getPath()
//            ),
//            wrappedType = mapper.mapModelToViewType(modelType.wrappedType)
//        ))
//    }
}

class ModuleGroupQueries(
    private val currentModuleName: ModuleName,
    private val group: ModuleGroup
) {
    fun populateTypes(typesWorldApi: TypesWorldApi, apiTypeFactory: ApiTypeFactory) {
        PrimitiveTypesPopulator().populate(typesWorldApi)
        ApiTypesPopulator(getModulesRecursive(group)).populate(typesWorldApi)
        B20FrontendTypesPopulator().populate(typesWorldApi)
        ViewModelTypesPopulator(getModulesRecursive(group), apiTypeFactory).populate(typesWorldApi)
        ViewTypesPopulator(getModulesRecursive(group)).populate(typesWorldApi, apiTypeFactory)
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