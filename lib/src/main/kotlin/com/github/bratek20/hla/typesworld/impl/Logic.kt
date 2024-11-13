package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.hla.typesworld.api.*

class TypesWorldApiLogic(
    populators: Set<TypesWorldPopulator>
): TypesWorldApi {
    private val classTypes: MutableList<ClassType> = mutableListOf()

    init {
        populators.sortedBy { it.getOrder() }.forEach {
            it.populate(this)
        }
    }

    override fun getTypeDependencies(type: HlaType): List<HlaType> {
        TODO("Not yet implemented")
    }

    override fun addClassType(type: ClassType): Unit {
        classTypes.add(type)
    }

    override fun addConcreteWrapper(type: ConcreteWrapper): Unit {
        TODO("Not yet implemented")
    }

    override fun addConcreteParametrizedClass(type: ConcreteParametrizedClass): Unit {
        TODO("Not yet implemented")
    }

    override fun getClassType(type: HlaType): ClassType {
        return classTypes.first { it.getType() == type }
    }
}