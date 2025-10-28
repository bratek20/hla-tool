package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.architecture.structs.api.StructPath
import com.github.bratek20.hla.typesworld.api.*
import java.util.LinkedList

fun WorldType.getFullName(): String {
    return "${getPath().value}/${getName()}"
}

class TraversedPathContext {
    private var traversedPaths: LinkedList<String> = LinkedList()
    private var traversedFieldsType: MutableList<WorldType> = mutableListOf()

    fun addTraversedPathAndFieldType(path: String, type: WorldType) {
        traversedPaths.add(path)
        traversedFieldsType.add(type)
    }

    fun getTraversedPath(): String {
        return traversedPaths.joinToString(separator = "")
    }

    fun removeLastPathAndFieldType() {
        removeLastPath()
        removeLastTraversedFieldType()
    }

    private fun removeLastPath() {
        if (traversedPaths.isNotEmpty()) {
            traversedPaths.removeLast()
        }
    }
    private fun removeLastTraversedFieldType() {
        if (traversedFieldsType.isNotEmpty()) {
            traversedFieldsType.removeLast()
        }
    }

    fun getTraversedFieldsType(): List<WorldType> {
        return traversedFieldsType.toList()
    }
}
class TypesWorldApiLogic: TypesWorldApi {
    companion object {
        private val wrappers = setOf("List", "Optional")
    }

    private val allTypes: MutableSet<WorldType> = mutableSetOf()
    private val primitives: MutableList<WorldType> = mutableListOf()
    private val classTypes: MutableList<WorldClassType> = mutableListOf()
    private val concreteParametrizedClasses: MutableList<WorldConcreteParametrizedClass> = mutableListOf()

    override fun ensureType(type: WorldType) {
        allTypes.firstOrNull {
            it.getName() == type.getName() && it.getPath() != type.getPath()
        }?.let {
            throw SameNameTypeExistsException(
                "Can not ensure '${type.getFullName()}'. Type '${type.getName()}' already exists for different path '${it.getPath()}'"
            )
        }
        allTypes.add(type)
    }

    override fun hasType(type: WorldType): Boolean {
        return allTypes.contains(type) || isWrapper(type)
    }

    override fun hasTypeByName(name: WorldTypeName): Boolean {
        return allTypes.any { it.getName() == name } || wrappers.any { tryExtractWrappedTypeNameForWrapper(name, it) != null }
    }

    override fun getTypeDependencies(type: WorldType): List<WorldType> {
        throwIfTypeNotFound(type)

        val direct = getDirectDependencies(type)
        return direct + direct.flatMap {
            getIndirectDependencies(it)
        }
    }

    private fun getIndirectDependencies(type: WorldType): List<WorldType> {
        concreteParametrizedClasses.firstOrNull { it.getType() == type }?.let {
            return it.getTypeArguments()
        }
        if (isWrapper(type)) {
            return listOf(getConcreteWrapper(type).getWrappedType())
        }
        return emptyList()
    }

    private fun getDirectDependencies(type: WorldType): List<WorldType> {
        classTypes.firstOrNull {
            it.getType() == type
        }?.let { classType ->
            val extendDependency = classType.getExtends()?.let {
                listOf(it)
            } ?: emptyList()
            val fieldDependencies = classType.getFields().map {
                it.getType()
            }
            return extendDependency + fieldDependencies
        }

        concreteParametrizedClasses.firstOrNull {
            it.getType() == type
        }?.let {
            return it.getTypeArguments()
        }

        if (isWrapper(type)) {
            return listOf(getConcreteWrapper(type).getWrappedType())
        }

        return emptyList()
    }

    override fun getAllTypes(): List<WorldType> {
        return allTypes.toList()
    }

    override fun getAllReferencesOf(target: WorldType, searchFor: WorldType): List<StructPath> {
        val traversedPathContext = TraversedPathContext()
        traversedPathContext.addTraversedPathAndFieldType("", target)
        return getAllReferencesOfFor(target, searchFor, traversedPathContext).map {
            StructPath(dropSlashIfPresent(it))
        }
    }

    override fun getAllClassTypes(): List<WorldClassType> {
        return classTypes.toList()
    }

    private fun dropSlashIfPresent(path: String): String {
        return if (path.endsWith("/")) {
            path.dropLast(1)
        } else {
            path
        }
    }

    private fun getAllReferencesOfFor(target: WorldType, searchFor: WorldType, traversedPathContext: TraversedPathContext): List<String> {
        if (target == searchFor) {
            val traversedPath = traversedPathContext.getTraversedPath()
            return listOf(traversedPath)
        }

        val kind = getTypeInfo(target).getKind()

        if (kind == WorldTypeKind.ClassType) {
            return getAllReferencesFromTargetFields(traversedPathContext, searchFor, target)
        }

        if (kind == WorldTypeKind.ConcreteWrapper) {
            val wrappedType = getConcreteWrapper(target).getWrappedType()

            val innerPaths = getAllReferencesOfFor(wrappedType, searchFor, TraversedPathContext())

            return innerPaths.map { innerPath ->
                val traversedPath = traversedPathContext.getTraversedPath()

                when {
                    isOptionalListWrapper(target) -> {
                        dropSlashIfPresent(traversedPath) + "?/[*]/$innerPath"
                    }
                    isListWrapper(target) -> "${traversedPath}[*]/$innerPath"
                    isOptionalWrapper(target) -> {
                        dropSlashIfPresent(traversedPath) + "?/$innerPath"
                    }
                    else -> throw IllegalStateException("Unknown wrapper type: ${target.getName().value}")
                }
            }
        }

        return emptyList()
    }

    private fun getAllReferencesFromTargetFields(
        traversedPathContext: TraversedPathContext,
        searchFor: WorldType,
        target: WorldType
    ): List<String> {
        val fields = getClassType(target).getFields()

        val selfReferencingFields = fields.filter{field ->
            (tryExtractWrappedTypeName(field.getType().getName()) ?: field.getType().getName()) == target.getName()
        }

        val traversedPathBeforeClean = traversedPathContext.getTraversedPath()
        return fields
            .filter { !selfReferencingFields.contains(it) }
            .flatMap { field ->
                traversedPathContext.addTraversedPathAndFieldType(field.getName() + "/", field.getType())
                val result = getAllReferencesOfFor(field.getType(), searchFor, traversedPathContext)
                //Backtrack: remove already added fields after result calculation
                traversedPathContext.removeLastPathAndFieldType()
                result
            } + selfReferencingFields.map { field ->
            val finalTraversedPath = traversedPathBeforeClean.isNotEmpty()
                .let { if (it) "$traversedPathBeforeClean/${field.getName()}" else field.getName() }
            when {
                isListWrapper(field.getType()) -> "${finalTraversedPath}/[*]/value"
                isOptionalWrapper(field.getType()) -> {
                    dropSlashIfPresent(finalTraversedPath) + "?/value"
                }

                else -> throw SelfReferenceDetectedException("Self referencing class should be Optional or List: ${target.getName().value}")
            }
        }
    }

    private fun isListWrapper(type: WorldType): Boolean {
        return type.getName().value.startsWith("List<")
    }

    private fun isOptionalWrapper(type: WorldType): Boolean {
        return type.getName().value.startsWith("Optional<")
    }

    private fun isOptionalListWrapper(type: WorldType): Boolean {
        return type.getName().value.startsWith("Optional<List<")
    }


    override fun addPrimitiveType(type: WorldType) {
        primitives.add(type)
        ensureType(type)
    }

    override fun addClassType(type: WorldClassType): Unit {
        classTypes.add(type)

        ensureType(type.getType())
        type.getExtends()?.let {
            ensureType(it)
        }
        type.getFields().forEach {
            ensureType(it.getType())
        }
    }

    override fun addConcreteParametrizedClass(type: WorldConcreteParametrizedClass): Unit {
        concreteParametrizedClasses.add(type)

        ensureType(type.getType())
        type.getTypeArguments().forEach {
            ensureType(it)
        }
    }

    override fun getClassType(type: WorldType): WorldClassType {
        return classTypes.firstOrNull { it.getType() == type }
            ?: throw WorldTypeNotFoundException("Class type '${type.getFullName()}' not found")
    }

    override fun getConcreteParametrizedClass(type: WorldType): WorldConcreteParametrizedClass {
        return concreteParametrizedClasses.firstOrNull { it.getType() == type }
            ?: throw WorldTypeNotFoundException("Concrete parametrized class type '${type.getFullName()}' not found")
    }

    override fun getConcreteWrapper(type: WorldType): WorldConcreteWrapper {
        val wrappedTypeName = tryExtractWrappedTypeName(type.getName())
            ?: throw WorldTypeNotFoundException("Type '${type.getFullName()}' is not a wrapper")

        val wrappedType = getTypeByName(wrappedTypeName)
        return WorldConcreteWrapper.create(
            type = type,
            wrappedType = wrappedType
        )
    }

    override fun getTypeByName(name: WorldTypeName): WorldType {
        return findTypeByNameForWrappers(name)
            ?: allTypes.firstOrNull { it.getName() == name }
            ?: throw WorldTypeNotFoundException("Hla type with name '${name}' not found")
    }

    override fun getTypeInfo(type: WorldType): WorldTypeInfo {
        if (findTypeByNameForWrappers(type.getName()) != null) {
            return WorldTypeInfo.create(
                kind = WorldTypeKind.ConcreteWrapper
            )
        }

        throwIfTypeNotFound(type)

        val kind = when {
            primitives.contains(type) -> WorldTypeKind.Primitive
            classTypes.any { it.getType() == type } -> WorldTypeKind.ClassType
            concreteParametrizedClasses.any { it.getType() == type } -> WorldTypeKind.ConcreteParametrizedClass
            else -> WorldTypeKind.Primitive
        }

        return WorldTypeInfo.create(
            kind = kind
        )
    }

    private fun throwIfTypeNotFound(type: WorldType) {
        if (!hasType(type)) {
            throw WorldTypeNotFoundException("Type '${type.getFullName()}' not found")
        }
    }

    private fun isWrapper(type: WorldType): Boolean {
        return tryExtractWrappedTypeName(type.getName()) != null
    }

    private fun findTypeByNameForWrappers(name: WorldTypeName): WorldType? {
        return wrappers.firstNotNullOfOrNull { findTypeByNameForWrapper(name, it) }
    }

    private fun findTypeByNameForWrapper(name: WorldTypeName, wrapper: String): WorldType? {
        if (name.value.startsWith("$wrapper<")) {
            val wrappedTypeName = name.value.removePrefix("$wrapper<").removeSuffix(">")
            val wrappedType = getTypeByName(WorldTypeName(wrappedTypeName))

            return WorldType.create(
                name = name,
                path = wrappedType.getPath()
            )
        }
        return null
    }

    private fun tryExtractWrappedTypeName(name: WorldTypeName): WorldTypeName? {
        val extractedOptional = tryExtractWrappedTypeNameForWrapper(name, "Optional")
        return if(extractedOptional == null) {
            tryExtractWrappedTypeNameForWrapper(name, "List")
        }else {
            tryExtractWrappedTypeNameForWrapper(extractedOptional, "List") ?: extractedOptional
        }
    }

    private fun tryExtractWrappedTypeNameForWrapper(name: WorldTypeName, wrapper: String): WorldTypeName? {
        if (name.value.startsWith("$wrapper<")) {
            val wrappedTypeName = name.value.removePrefix("$wrapper<").removeSuffix(">")
            return WorldTypeName(wrappedTypeName)
        }
        return null
    }
}