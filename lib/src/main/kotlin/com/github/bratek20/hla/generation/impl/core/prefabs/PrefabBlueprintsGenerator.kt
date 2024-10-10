package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.view.ContainerViewLogic
import com.github.bratek20.hla.generation.impl.core.view.ElementViewLogic
import com.github.bratek20.hla.generation.impl.core.view.WindowViewLogic
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

abstract class PrefabContainerBlueprintLogic(
    private val view: ContainerViewLogic,
) {
    abstract fun getName(): String
    abstract fun getMyFullType(): String
    abstract fun blueprintType(): BlueprintType

    protected fun getFullType(viewModelTypeName: String): String {
        return view.mapper.mapViewModelToFullViewTypeName(viewModelTypeName)
    }

    fun getFile(): File {
        val blueprint = PrefabBlueprint.create(
            blueprintType = blueprintType(),
            name = getName(),
            viewType = getMyFullType(),
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

class PrefabElementBlueprintLogic(
    private val view: ElementViewLogic,
): PrefabContainerBlueprintLogic(view) {
    override fun getName(): String {
        return view.elem.modelType.name()
    }

    override fun getMyFullType(): String {
        return getFullType(view.getViewModelTypeName())
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.UiElement
    }
}

class PrefabWindowBlueprintLogic(
    private val view: WindowViewLogic,
): PrefabContainerBlueprintLogic(view) {
    override fun getName(): String {
        return view.window.getClassName()
    }

    override fun getMyFullType(): String {
        return view.window.getModuleName() + ".View." + view.getViewClassName()
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.Window
    }
}

class PrefabBlueprintsGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun generateFiles(): List<File> {
        val mapper = logic.mapper()
        val viewElementLogic = logic.elementsLogic().map { ElementViewLogic(it, mapper) }
        val viewWindowLogic = logic.windowsLogic().map { WindowViewLogic(it, mapper) }

        val elementBlueprintLogic = viewElementLogic.map { PrefabElementBlueprintLogic(it) }
        val windowBlueprintLogic = viewWindowLogic.map { PrefabWindowBlueprintLogic(it) }

        return elementBlueprintLogic.map { it.getFile() } +
                windowBlueprintLogic.map { it.getFile() }
    }
}