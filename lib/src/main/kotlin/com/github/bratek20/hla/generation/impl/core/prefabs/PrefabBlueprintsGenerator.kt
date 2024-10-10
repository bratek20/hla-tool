package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.view.*
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

abstract class PrefabBaseBlueprintLogic(
    private val mapper: ModelToViewModelTypeMapper,
) {
    abstract fun getName(): String
    abstract fun getMyFullType(): String
    abstract fun blueprintType(): BlueprintType
    abstract fun getBlueprint(): PrefabBlueprint

    protected fun getFullType(viewModelTypeName: String): String {
        return mapper.mapViewModelToFullViewTypeName(viewModelTypeName)
    }

    fun getFile(): File {
        val blueprint = getBlueprint()

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

class PrefabWrappedElementBlueprintLogic(
    private val view: WrappedElementViewLogic,
): PrefabBaseBlueprintLogic(view.mapper) {
    override fun getName(): String {
        return view.getViewClassName().replace("View", "")
    }

    override fun getMyFullType(): String {
        return view.modelType.moduleName() + ".View." + view.getViewClassName()
    }

    override fun blueprintType(): BlueprintType {
        return if (view is ElementGroupViewLogic) {
            BlueprintType.ElementGroup
        } else {
            BlueprintType.OptionalElement
        }
    }

    override fun getBlueprint(): PrefabBlueprint {
        return PrefabBlueprint.create(
            blueprintType = blueprintType(),
            name = getName(),
            viewType = getMyFullType(),
            creationOrder = 1,
            elementViewType = getFullType(view.getElementViewModelTypeName())
        )
    }
}

abstract class PrefabContainerBlueprintLogic(
    private val view: ContainerViewLogic,
): PrefabBaseBlueprintLogic(view.mapper) {

    override fun getBlueprint(): PrefabBlueprint {
        return PrefabBlueprint.create(
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
        val viewElementGroupLogic = logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it, mapper) }
        val viewElementOptionalLogic = logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper) }

        val elementBlueprintLogic = viewElementLogic.map { PrefabElementBlueprintLogic(it) }
        val windowBlueprintLogic = viewWindowLogic.map { PrefabWindowBlueprintLogic(it) }
        val elementGroupBlueprintLogic = viewElementGroupLogic.map { PrefabWrappedElementBlueprintLogic(it) }
        val elementOptionalBlueprintLogic = viewElementOptionalLogic.map { PrefabWrappedElementBlueprintLogic(it) }

        return elementBlueprintLogic.map { it.getFile() } +
                windowBlueprintLogic.map { it.getFile() } +
                elementGroupBlueprintLogic.map { it.getFile() } +
                elementOptionalBlueprintLogic.map { it.getFile() }
    }
}