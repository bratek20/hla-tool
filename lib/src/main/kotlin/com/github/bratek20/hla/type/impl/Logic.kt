package com.github.bratek20.hla.type.impl

import com.github.bratek20.hla.type.api.*

class TypeApiLogic: TypeApi {
    override fun getTypeDependencies(type: HlaType): List<HlaType> {
        if (type.getName() == "SomeClass2GroupView") {
            return listOf(
                HlaType.create(
                    "SomeClass2View",
                    type.getPath()
                )
            )
        }
        if (type.getName() == "SomeClassGroupView") {
            return listOf(
                HlaType.create(
                    "SomeClassView",
                    type.getPath()
                )
            )
        }
        if (type.getName() == "OptionalSomeClassView") {
            return listOf(
                HlaType.create(
                    "SomeClassView",
                    type.getPath()
                )
            )
        }

        return emptyList()
    }
}