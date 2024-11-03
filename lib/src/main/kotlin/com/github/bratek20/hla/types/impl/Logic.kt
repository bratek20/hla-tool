package com.github.bratek20.hla.types.impl

import com.github.bratek20.hla.types.api.*

class TypeApiLogic(
    private val wrappers: List<Wrapper>,
    private val structures: List<Structure>
): TypeApi {

    override fun getTypeDependencies(type: HlaType): List<HlaType> {
        wrappers.firstOrNull {
            it.getType() == type
        }?.let {
            return listOf(
                it.getWrappedType()
            )
        }

        structures.firstOrNull {
            it.getType() == type
        }?.let { structure ->
            return structure.getFields().map {
                it.getType()
            }
        }

        //TODO-GENERALIZE
        if (type.getName() == "SomeClass3View") {
            return listOf(
                HlaType.create(
                    "SomeClass2View",
                    type.getPath()
                ),
                HlaType.create(
                    "SomeEnumSwitchView",
                    type.getPath()
                ),
                HlaType.create(
                    "SomeClass2GroupView",
                    type.getPath()
                )
            )
        }
        if (type.getName() == "SomeClass6View") {
            return listOf(
                HlaType.create(
                    "OptionalSomeClassView",
                    type.getPath()
                ),
                HlaType.create(
                    "SomeClass2GroupView",
                    type.getPath()
                )
            )
        }
        if (type.getName() == "SomeWindowView") {
            return listOf(
                HlaType.create(
                    "SomeClassView",
                    type.getPath()
                ),
                HlaType.create(
                    "SomeClassGroupView",
                    type.getPath()
                )
            )
        }

        return emptyList()
    }
}