package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.view.ElementViewLogic
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

class PrefabBlueprintLogic(
    private val view: ElementViewLogic,
) {
    private fun getName(): String {
        return view.elem.modelType.name()
    }

    private fun getFullType(viewModelTypeName: String): String {
        return view.mapper.mapViewModelToFullViewTypeName(viewModelTypeName)
    }

    fun getFile(): File {
        val blueprint = PrefabBlueprint.create(
            blueprintType = BlueprintType.UiElement,
            name = getName(),
            viewType = getFullType(view.getViewModelTypeName()),
            creationOrder = 1,
            children = view.getFields().map {
                PrefabChildBlueprint.create(
                    name = it.name,
                    viewType = getFullType(it.typeName)
                )
            }
        )

        val serializer = SerializationFactory.createSerializer(SerializerConfig.create(
            readable = true,
        ))

        val serialized = serializer.serialize(blueprint)
        return File.create(
            name = FileName("${getName()}.json"),
            content = FileContent.fromString(serialized.getValue())
        )
    }
}
class PrefabBlueprintsGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun generateFiles(): List<File> {
        val mapper = logic.mapper()
        val viewLogic = logic.elementsLogic().map { ElementViewLogic(it, mapper) }
        val prefabBlueprints = viewLogic.map { PrefabBlueprintLogic(it) }
        return prefabBlueprints.map { it.getFile() }
    }
}