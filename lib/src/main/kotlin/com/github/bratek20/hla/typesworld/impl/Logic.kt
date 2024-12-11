package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.architecture.structs.api.StructPath
import com.github.bratek20.hla.typesworld.api.*

fun WorldType.getFullName(): String {
    return "${getPath().value}/${getName()}"
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
        if (target == searchFor) {
            return listOf(StructPath(""))
        }

        val kind = getTypeInfo(target).getKind()

        if (kind == WorldTypeKind.ClassType) {
            return getClassType(target).getFields().flatMap { field ->
                getAllReferencesOf(searchFor, field.getType()).map {
                    toStructPath("${field.getName()}/$it")
                }
            }
        }

        if (kind == WorldTypeKind.ConcreteWrapper) {
            return getConcreteWrapper(target).getWrappedType().let { wrappedType ->
                getAllReferencesOf(wrappedType, searchFor).map {
                    toStructPath("[*]/$it")
                }
            }
        }

        return emptyList()
    }

    private fun toStructPath(path: String): StructPath {
        if (path.endsWith("/")) {
            return StructPath(path.dropLast(1))
        }
        return StructPath(path)
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
        return wrappers.firstNotNullOfOrNull { tryExtractWrappedTypeNameForWrapper(name, it) }
    }

    private fun tryExtractWrappedTypeNameForWrapper(name: WorldTypeName, wrapper: String): WorldTypeName? {
        if (name.value.startsWith("$wrapper<")) {
            val wrappedTypeName = name.value.removePrefix("$wrapper<").removeSuffix(">")
            return WorldTypeName(wrappedTypeName)
        }
        return null
    }
}