package com.github.bratek20.hla.importscalculation.impl

import com.github.bratek20.hla.importscalculation.api.*

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.isHla
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType

class ImportsCalculatorLogic(
    private val typesWorldApi: TypesWorldApi
): ImportsCalculator {
    override fun calculate(module: ModuleName, submodule: SubmoduleName): List<String> {
        val types = typesWorldApi.getAllTypes()
            .filter { it.getPath().isHla() }
            .filter {
                val path = it.getPath().asHla()
                path.getModuleName() == module &&path.getSubmoduleName() == submodule
            }

        val dependencies = types.flatMap { typesWorldApi.getTypeDependencies(it) }

        return dependencies.map { mapToImport(it) }
    }

    private fun mapToImport(type: WorldType): String {
        return type.getPath().asParts().joinToString(".")
    }
}