package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.hlatypesworld.impl.ApiTypesPopulator
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

class TrackingTypesPopulator(
    private val world: TypesWorldApi,
): HlaTypesWorldPopulator {
    companion object {
        const val ORDER = ApiTypesPopulator.ORDER + 1
    }
    override fun getOrder(): Int {
        return ORDER
    }

    override fun populate(modules: List<ModuleDefinition>) {
        modules.forEach { module ->
            module.getTrackingSubmodule()?.let { subModule ->
                subModule.getDimensions().forEach { populateTable(module, it) }
                subModule.getEvents().forEach { populateTable(module, it) }
            }
        }
    }
    private fun populateTable(
        module: ModuleDefinition,
        def: TableDefinition
    ) {
        val path = HlaTypePath.create(
            module.getName(),
            SubmoduleName.Impl,
            PatternName.Track
        ).asWorld()

        world.ensureType(
            WorldType.create(
                name = WorldTypeName(def.getName()),
                path = path
            )
        )

        //TODO: Hardcode here tracking dimension concept
    }
}