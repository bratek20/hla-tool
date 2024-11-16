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
        allTypes.add(type)
    }

    override fun hasType(type: WorldType): Boolean {
        return allTypes.contains(type)
    }

    override fun getTypeDependencies(type: WorldType): List<WorldType> {
        classTypes.firstOrNull {
            it.getType() == type
        }?.let { classType ->
            return classType.getFields().map {
                it.getType()
            }
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

    override fun addPrimitiveType(type: WorldType) {
        primitives.add(type)
        ensureType(type)
    }

    override fun addClassType(type: WorldClassType): Unit {
        classTypes.add(type)

        ensureType(type.getType())
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
        return allTypes.firstOrNull { it.getName() == name }
            ?: throw WorldTypeNotFoundException("Hla type with name '${name}' not found")
    }
}