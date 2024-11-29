package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

class PrimitiveTypesPopulator(
    private val api: TypesWorldApi
): HlaTypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    override fun populate(modules: List<ModuleDefinition>) {
        BaseType.entries.forEach {
            val path = HlaTypePath.create(
                GroupName("Language"),
                ModuleName("Types"),
                SubmoduleName.Api,
                PatternName.Primitives
            ).asWorld()

            api.addPrimitiveType(
                WorldType.create(
                    name = WorldTypeName(it.name.lowercase()),
                    path = path
                )
            )
            api.ensureType(
                WorldType.create(
                    name = WorldTypeName("List<${it.name.lowercase()}>"),
                    path = path
                )
            )
            api.ensureType(
                WorldType.create(
                    name = WorldTypeName("Optional<${it.name.lowercase()}>"),
                    path = path
                )
            )
        }
    }
}