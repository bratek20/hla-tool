package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeKind

class CreationOrderCalculator(
    private val typesWorldApi: TypesWorldApi,
) {
    private val cache = mutableMapOf<WorldType, Int>()

    fun calculateCreationOrder(type: WorldType): Int {
        if (type.getPath().value.startsWith("B20/Frontend")) {
            return 0
        }
        if (typesWorldApi.getTypeInfo(type).getKind() == WorldTypeKind.ConcreteParametrizedClass) {
            return 0
        }

        return cache.getOrPut(type) {
            val dependencies = typesWorldApi.getTypeDependencies(type)
            if (dependencies.isEmpty()) {
                1
            } else {
                dependencies.map { calculateCreationOrder(it) }.max() + 1
            }
        }
    }
}