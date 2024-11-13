package com.github.bratek20.hla.typesworld.impl

import com.github.bratek20.hla.typesworld.api.*

fun ClassType.getType(): HlaType {
    return HlaType.create(
        kind = HlaTypeKind.ClassType,
        name = getName(),
        path = getPath()
    )
}

fun ConcreteWrapper.getType(): HlaType {
    return HlaType.create(
        kind = HlaTypeKind.ConcreteWrapper,
        name = getName(),
        path = getPath()
    )
}

class TypesWorldApiLogic(
    populators: Set<TypesWorldPopulator>
): TypesWorldApi {
    private val classTypes: MutableList<ClassType> = mutableListOf()
    private val concreteWrappers: MutableList<ConcreteWrapper> = mutableListOf()

    init {
        populators.sortedBy { it.getOrder() }.forEach {
            it.populate(this)
        }
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