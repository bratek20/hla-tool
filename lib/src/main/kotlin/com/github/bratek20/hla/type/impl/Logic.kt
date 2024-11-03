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