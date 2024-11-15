package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.architecture.serialization.api.SerializerConfig
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.view.*
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.ModelToViewModelTypeMapper
import com.github.bratek20.hla.prefabcreator.api.BlueprintType
import com.github.bratek20.hla.prefabcreator.api.PrefabBlueprint
import com.github.bratek20.hla.prefabcreator.api.PrefabChildBlueprint
import com.github.bratek20.hla.typesworld.api.HlaType
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

abstract class PrefabBaseBlueprintLogic(
    private val mapper: ModelToViewModelTypeMapper,
    private val typesWorldApi: TypesWorldApi
) {
    abstract fun getName(): String
    abstract fun getMyType(): HlaType
    abstract fun blueprintType(): BlueprintType

    open fun children(): List<PrefabChildBlueprint>? = null
    open fun elementViewType(): String? = null

    protected fun getFullType(viewModelTypeName: String): String {
        return mapper.mapViewModelToFullViewTypeName(viewModelTypeName)
    }

    private fun asFullViewType(type: HlaType): String {
        return type.getPath().dropPatternPart().replace("/", ".") + "." + type.getName()
    }

    fun getFile(): File {
        val calculator = CreationOrderCalculator(typesWorldApi)
        val type = getMyType()

        val blueprint = PrefabBlueprint.create(
            blueprintType = blueprintType(),
            name = getName(),
            viewType = asFullViewType(type),
            creationOrder = calculator.calculateCreationOrder(type),
            children = children() ?: emptyList(),
            elementViewType = elementViewType()
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
): PrefabBaseBlueprintLogic(view.mapper, typesWorldApi) {
    override fun getName(): String {
        return view.getViewClassName().replace("View", "")
    }

    override fun getMyType(): HlaType {
        return view.getViewClassType()
    }

    override fun blueprintType(): BlueprintType {
        return if (view is ElementGroupViewLogic) {
            BlueprintType.ElementGroup
        } else {
            BlueprintType.OptionalElement
        }
    }

    override fun elementViewType(): String {
        return getFullType(view.getElementViewModelTypeName())
    }
}

abstract class PrefabContainerBlueprintLogic(
    private val view: ContainerViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabBaseBlueprintLogic(view.mapper, typesWorldApi) {

    override fun children(): List<PrefabChildBlueprint>? {
        return view.getFields().map {
            PrefabChildBlueprint.create(
                name = it.name,
                viewType = getFullType(it.typeName)
            )
        }
    }
}

class PrefabComplexElementBlueprintLogic(
    private val view: ComplexElementViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabContainerBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        return view.elem.modelType.name()
    }

    override fun getMyType(): HlaType {
        return view.getViewClassType()
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.ComplexElement
    }
}

class PrefabWindowBlueprintLogic(
    private val view: WindowViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabContainerBlueprintLogic(view, typesWorldApi) {
    override fun getName(): String {
        return view.window.getClassName()
    }

    override fun getMyType(): HlaType {
        return view.getViewClassType()
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.Window
    }
}

class PrefabEnumElementBlueprintLogic(
    private val view: EnumElementViewLogic,
    typesWorldApi: TypesWorldApi
): PrefabBaseBlueprintLogic(view.mapper, typesWorldApi) {
    override fun getName(): String {
        return view.vmLogic.modelType.name()
    }

    override fun getMyType(): HlaType {
        return view.getViewClassType()
    }

    override fun blueprintType(): BlueprintType {
        return BlueprintType.EnumElement
    }
}

class PrefabBlueprintsGenerator(
    private val typesWorldApi: TypesWorldApi
): BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFiles(): List<File> {
        val mapper = logic.mapper()
        val viewComplexElementLogic = logic.complexElementsLogic().map { ComplexElementViewLogic(it, mapper) }
        val viewWindowLogic = logic.windowsLogic().map { WindowViewLogic(it, mapper) }
        val viewElementGroupLogic = logic.elementListTypesToGenerate().map { ElementGroupViewLogic(it, mapper) }
        val viewElementOptionalLogic = logic.elementOptionalTypesToGenerate().map { OptionalElementViewLogic(it, mapper) }
        val viewEnumElementLogic = logic.enumElementsLogic().map { EnumElementViewLogic(it, mapper) }

        (viewComplexElementLogic + viewWindowLogic + viewElementGroupLogic + viewElementOptionalLogic + viewEnumElementLogic)
            .forEach { it.populateType(typesWorldApi) }

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