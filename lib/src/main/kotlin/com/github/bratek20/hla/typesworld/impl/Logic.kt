package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.hla.typesworld.api.*

fun HlaType.getFullName(): String {
    return "${getPath().value}/${getName()}"
}

class TypesWorldApiLogic(
    populators: Set<TypesWorldPopulator>
): TypesWorldApi {
    private val allTypes: MutableSet<HlaType> = mutableSetOf()
    private val primitives: MutableList<HlaType> = mutableListOf()
    private val classTypes: MutableList<ClassType> = mutableListOf()
    private val concreteWrappers: MutableList<ConcreteWrapper> = mutableListOf()

    init {
        populators.sortedBy { it.getOrder() }.forEach {
            populate(it)
        }
    }

    override fun populate(populator: TypesWorldPopulator) {
        populator.populate(this)
    }

    override fun ensureType(type: HlaType) {
        allTypes.add(type)
    }

    override fun hasType(type: HlaType): Boolean {
        return allTypes.contains(type)
    }

    override fun getTypeDependencies(type: HlaType): List<HlaType> {
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

        return emptyList()
    }

    override fun addPrimitiveType(type: HlaType) {
        primitives.add(type)
        ensureType(type)
    }

    override fun addClassType(type: ClassType): Unit {
        classTypes.add(type)
        ensureType(type.getType())
    }

    override fun addConcreteWrapper(type: ConcreteWrapper): Unit {
        concreteWrappers.add(type)
        ensureType(type.getType())
    }

    override fun addConcreteParametrizedClass(type: ConcreteParametrizedClass): Unit {
        TODO("Not yet implemented")
    }

    override fun getClassType(type: HlaType): ClassType {
        return classTypes.firstOrNull { it.getType() == type }
            ?: throw TypeNotFoundException("Class type '${type.getFullName()}' not found")
    }

    override fun getTypeByName(name: HlaTypeName): HlaType {
        return allTypes.firstOrNull { it.getName() == name }
            ?: throw TypeNotFoundException("Hla type with name '${name}' not found")
    }
}