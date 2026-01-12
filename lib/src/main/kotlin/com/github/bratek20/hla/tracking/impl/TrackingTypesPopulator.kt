package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleName
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
        val TRACKING_DIMENSION_WORLD_TYPE: WorldType = WorldType.create(
            name = WorldTypeName("TrackingDimension"),
            path = HlaTypePath.create(
                ModuleName("Tracking"),
                SubmoduleName.Impl,
                PatternName.Interfaces
            ).asWorld()
        )

        val TRACKING_DIMENSION_LIST_WORLD_TYPE: WorldType = WorldType.create(
            name = WorldTypeName("TrackingDimensionList"),
            path = HlaTypePath.create(
                ModuleName("Tracking"),
                SubmoduleName.Impl,
                PatternName.Interfaces
            ).asWorld()
        )
    }
    override fun getOrder(): Int {
        return ORDER
    }

    override fun populate(modules: List<ModuleDefinition>) {
        world.ensureType(
            TRACKING_DIMENSION_WORLD_TYPE
        )
        world.ensureType(
            TRACKING_DIMENSION_LIST_WORLD_TYPE
        )
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
    }
}