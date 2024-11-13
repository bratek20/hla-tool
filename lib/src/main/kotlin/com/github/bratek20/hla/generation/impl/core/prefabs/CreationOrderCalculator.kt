package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.typesworld.api.HlaType
import com.github.bratek20.hla.typesworld.api.TypesWorldApi

class CreationOrderCalculator(
    private val typesWorldApi: TypesWorldApi,
) {
    private val cache = mutableMapOf<HlaType, Int>()

    fun calculateCreationOrder(type: HlaType): Int {
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