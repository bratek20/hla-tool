package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.types.api.HlaType
import com.github.bratek20.hla.types.api.TypesApi

class CreationOrderCalculator(
    private val typeApi: TypesApi,
) {
    private val cache = mutableMapOf<HlaType, Int>()

    fun calculateCreationOrder(type: HlaType): Int {
        return cache.getOrPut(type) {
            val dependencies = typeApi.getTypeDependencies(type)
            if (dependencies.isEmpty()) {
                1
            } else {
                dependencies.map { calculateCreationOrder(it) }.max() + 1
            }
        }
    }
}