package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.hla.typesworld.api.*

class TypesWorldApiLogic(
    populators: Set<TypesWorldPopulator>
): TypesWorldApi {
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

    override fun hasType(type: HlaType): Boolean {
        return getAllTypes().any { it == type }
    }

    private fun getAllTypes(): List<HlaType> {
        return primitives +
            classTypes.map { it.getType() } +
            concreteWrappers.map { it.getType() }
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
    }

    override fun addClassType(type: ClassType): Unit {
        classTypes.add(type)
    }

    override fun addConcreteWrapper(type: ConcreteWrapper): Unit {
        concreteWrappers.add(type)
    }

    override fun addConcreteParametrizedClass(type: ConcreteParametrizedClass): Unit {
        TODO("Not yet implemented")
    }

    override fun getClassType(type: HlaType): ClassType {
        return classTypes.firstOrNull { it.getType() == type }
            ?: throw TypeNotFoundException("Class type '${type.getName()}' not found")
    }
}