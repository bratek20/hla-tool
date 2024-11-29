package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.hla.typesworld.api.*

fun WorldType.getFullName(): String {
    return "${getPath().value}/${getName()}"
}

class TypesWorldApiLogic: TypesWorldApi {
    private val allTypes: MutableSet<WorldType> = mutableSetOf()
    private val primitives: MutableList<WorldType> = mutableListOf()
    private val classTypes: MutableList<WorldClassType> = mutableListOf()
    private val concreteWrappers: MutableList<WorldConcreteWrapper> = mutableListOf()
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
        return allTypes.contains(type)
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
        concreteWrappers.firstOrNull { it.getType() == type }?.let {
            return listOf(it.getWrappedType())
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

        concreteWrappers.firstOrNull {
            it.getType() == type
        }?.let {
            return listOf(
                it.getWrappedType()
            )
        }

        concreteParametrizedClasses.firstOrNull {
            it.getType() == type
        }?.let {
            return it.getTypeArguments()
        }

        return emptyList()
    }

    override fun getAllTypes(): List<WorldType> {
        return allTypes.toList()
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

    override fun addConcreteWrapper(type: WorldConcreteWrapper): Unit {
        concreteWrappers.add(type)

        ensureType(type.getType())
        ensureType(type.getWrappedType())
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

    override fun getTypeByName(name: WorldTypeName): WorldType {
        return getTypeByNameForWrapper(name, "List")
            ?: getTypeByNameForWrapper(name, "Optional")
            ?: allTypes.firstOrNull { it.getName() == name }
            ?: throw WorldTypeNotFoundException("Hla type with name '${name}' not found")
    }

    override fun getTypeInfo(type: WorldType): WorldTypeInfo {
        throwIfTypeNotFound(type)

        val kind = when {
            primitives.contains(type) -> WorldTypeKind.Primitive
            classTypes.any { it.getType() == type } -> WorldTypeKind.ClassType
            concreteWrappers.any { it.getType() == type } -> WorldTypeKind.ConcreteWrapper
            concreteParametrizedClasses.any { it.getType() == type } -> WorldTypeKind.ConcreteParametrizedClass
            else -> WorldTypeKind.Primitive
        }

        return WorldTypeInfo.create(
            kind = kind
        )
    }

    private fun throwIfTypeNotFound(type: WorldType) {
        if (!allTypes.contains(type)) {
            throw WorldTypeNotFoundException("Type '${type.getFullName()}' not found")
        }
    }

    private fun getTypeByNameForWrapper(name: WorldTypeName, wrapper: String): WorldType? {
        if (name.value.startsWith("$wrapper<")) {
            val wrappedTypeName = name.value.removePrefix("$wrapper<").removeSuffix(">")
            val wrappedType = allTypes.firstOrNull { it.getName().value == wrappedTypeName }
                ?: throw WorldTypeNotFoundException("Hla type with name '${wrappedTypeName}' not found")

            return WorldType.create(
                name = name,
                path = wrappedType.getPath()
            )
        }
        return null
    }
}