package com.github.bratek20.hla.types.impl

import com.github.bratek20.hla.types.api.HlaType
import com.github.bratek20.hla.types.api.Structure
import com.github.bratek20.hla.types.api.TypesApi
import com.github.bratek20.hla.types.api.Wrapper

class TypesApiLogic: TypesApi {
    private val structures: MutableList<Structure> = mutableListOf()
    private val wrappers: MutableList<Wrapper> = mutableListOf()

    override fun addStructure(structure: Structure) {
        structures.add(structure)
    }

    override fun addWrapper(wrapper: Wrapper) {
        wrappers.add(wrapper)
    }

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

        return emptyList()
    }
}