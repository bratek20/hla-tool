package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

class PrefabBlueprintsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun generateFiles(): List<File> {
        val blueprint = PrefabBlueprint.create(
            blueprintType = BlueprintType.UiElement,
            name = "OtherClass",
            viewType = "OtherModule.View.OtherClassView",
            creationOrder = 1,
            children = listOf(
                PrefabChildBlueprint.create(
                    name = "id",
                    viewType = "B20.Frontend.Elements.View.LabelView"
                ),
                PrefabChildBlueprint.create(
                    name = "amount",
                    viewType = "B20.Frontend.Elements.View.LabelView"
                )
            )
        )

        val serializer = SerializationFactory.createSerializer()
        val serialized = serializer.serialize(blueprint)
        return listOf(
            File.create(
                name = FileName("OtherClass.json"),
                content = FileContent.fromString(serialized.getValue())
            )
        )
    }
}