package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldClassType
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

class B20FrontendTypesPopulator(
    private val api: TypesWorldApi
): HlaTypesWorldPopulator {
    companion object {
        val emptyModelType = WorldType.create(
            name = WorldTypeName("EmptyModel"),
            path = HlaTypePath.create(
                listOf(
                    GroupName("B20"),
                    GroupName("ViewModel")
                ),
                ModuleName("UiElements"),
                SubmoduleName.Api,
                PatternName.ValueObjects
            ).asWorld()
        )

        val pathForUndefinedPattern = HlaTypePath.create(
            listOf(
                GroupName("B20"),
                GroupName("ViewModel")
            ),
            ModuleName("UiElements"),
            SubmoduleName.Api,
            PatternName.Undefined
        ).asWorld()

        val b20ViewModelTypes = listOf(
            "Label",
            "LabelGroup",
            "OptionalLabel",
            "Button",
            "BoolSwitch",
            "Toggle",
            "Animation",
            "InputField",
            "Image",
            "Scroll",
            "ProgressBar",
            "Timer",
            "SimpleGroup",
            "EmptyVm"
        )
    }

    override fun getOrder(): Int {
        return 0
    }

    override fun populate(modules: List<ModuleDefinition>) {
        api.addClassType(
            WorldClassType.create(
                type = emptyModelType,
                fields = emptyList()
            )
        )

        b20ViewModelTypes.forEach {
            add(it)
        }
    }

    private fun add(typeName: String) {
        api.addClassType(
            WorldClassType.create(
                type = WorldType.create(
                    name = WorldTypeName(typeName),
                    path = pathForUndefinedPattern
                ),
                fields = emptyList()
            )
        )
    }
}