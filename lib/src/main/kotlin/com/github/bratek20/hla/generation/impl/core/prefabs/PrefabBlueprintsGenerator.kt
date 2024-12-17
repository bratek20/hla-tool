package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.view.*
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElement
import com.github.bratek20.hla.mvvmtypesmappers.impl.getModelTypeForEnsuredUiElementWrapper
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

fun asFullViewType(type: WorldType): String {
    return type.getPath().asHla().dropPatternPart().replace("/", ".") + "." + type.getName()
}

abstract class PrefabBaseBlueprintLogic(
    private val view: ViewLogic,
    protected val typesWorldApi: TypesWorldApi
) {
    abstract fun getName(): String
    abstract fun blueprintType(): BlueprintType

    open fun children(): List<PrefabChildBlueprint>? = null
    open fun elementViewType(): WorldType? = null

    fun getFile(): File {
        val calculator = CreationOrderCalculator(typesWorldApi)
        val type = view.getViewType()

        val blueprint = PrefabBlueprint.create(
            blueprintType = blueprintType(),
            name = getName(),
            viewType = asFullViewType(type),
            creationOrder = calculator.calculateCreationOrder(type),
            children = children() ?: emptyList(),
            elementViewType = elementViewType()?.let { asFullViewType(it) }
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

class PrefabWrappedElementBlueprintLogic(
    private val view: WrappedElementViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabBaseBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        return view.getViewClassName().replace("View", "")
    }

    override fun blueprintType(): BlueprintType {
        return if (view.getViewType().getName().value.startsWith("Optional")) {
            BlueprintType.OptionalElement
        } else {
            BlueprintType.ElementGroup
        }
    }

    override fun elementViewType(): WorldType {
        return view.getElementViewType()
    }
}

abstract class PrefabContainerBlueprintLogic(
    private val view: ViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabBaseBlueprintLogic(view, typesWorldApi) {

    override fun children(): List<PrefabChildBlueprint>? {
        return view.getFields().map {
            PrefabChildBlueprint.create(
                name = it.getName(),
                viewType = asFullViewType(it.getType())
            )
        }
    }
}

class PrefabComplexElementBlueprintLogic(
    private val view: ViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabContainerBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        val model = getModelTypeForEnsuredUiElement(typesWorldApi, view.getViewModelTypeName())
        if(model.getName().value == "EmptyModel") {
            return view.getViewModelTypeName()
        }
        return model.getName().value
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.ComplexElement
    }
}

class PrefabWindowBlueprintLogic(
    private val view: ViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabContainerBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        return view.getViewModelTypeName()
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.Window
    }
}

class PrefabEnumElementBlueprintLogic(
    private val view: ViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabBaseBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        return getModelTypeForEnsuredUiElement(typesWorldApi, view.getViewModelTypeName()).getName().value
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.EnumElement
    }
}

class PrefabBlueprintsGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun generateFiles(): List<File> {
        val viewComplexElementLogic = logic.complexElementsLogic().map { ViewLogic(it) }
        val viewWindowLogic = logic.windowsLogic().map { ViewLogic(it) }
        val viewElementGroupLogic = logic.elementListTypesToGenerate().map { WrappedElementViewLogic(it) }
        val viewElementOptionalLogic = logic.elementOptionalTypesToGenerate().map { WrappedElementViewLogic(it) }
        val viewEnumElementLogic = logic.enumElementsLogic().map { ViewLogic(it) }

        val elementBlueprintLogic = viewComplexElementLogic.map { PrefabComplexElementBlueprintLogic(it, typesWorldApi) }
        val windowBlueprintLogic = viewWindowLogic.map { PrefabWindowBlueprintLogic(it, typesWorldApi) }
        val elementGroupBlueprintLogic = viewElementGroupLogic.map { PrefabWrappedElementBlueprintLogic(it, typesWorldApi) }
        val elementOptionalBlueprintLogic = viewElementOptionalLogic.map { PrefabWrappedElementBlueprintLogic(it, typesWorldApi) }
        val enumElementBlueprintLogic = viewEnumElementLogic.map { PrefabEnumElementBlueprintLogic(it, typesWorldApi) }

        return (elementBlueprintLogic +
                windowBlueprintLogic +
                elementGroupBlueprintLogic +
                elementOptionalBlueprintLogic +
                enumElementBlueprintLogic)
            .map { it.getFile() }
    }
}