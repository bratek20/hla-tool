package com.github.bratek20.hla.importscalculation.impl

import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.importscalculation.api.ImportsCalculator
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType

class ImportsCalculatorLogic(
    private val typesWorldApi: TypesWorldApi
): ImportsCalculator {
    override fun calculate(path: HlaTypePath): List<String> {
        val types = typesWorldApi.getAllTypes()
            .filter { it.getPath() == path.asWorld() }

        val dependencies = types.flatMap { typesWorldApi.getTypeDependencies(it) }

        return dependencies.map { mapToImport(it) }
    }

    private fun mapToImport(type: WorldType): String {
        return type.getPath().asParts().dropLast(1).joinToString(".")
    }
}