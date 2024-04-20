package pl.bratek20.hla.generation.impl.core.domain

import pl.bratek20.hla.model.*

interface ViewType {
    fun name(): String
}

data class BaseViewType(
    val name: BaseType,
    val languageTypes: LanguageTypes
) : ViewType {
    override fun name(): String {
        return languageTypes.mapBaseType(name)
    }
}

data class SimpleVOViewType(
    val name: String,
    val boxedType: BaseViewType
) : ViewType {
    override fun name(): String {
        return name;
    }
}


data class ComplexVOViewType(
    val name: String,
) : ViewType {
    override fun name(): String {
        return name
    }
}

data class ListViewType(
    val wrappedType: ViewType,
    val languageTypes: LanguageTypes
) : ViewType {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }
}

class ViewTypeFactory(
    private val languageTypes: LanguageTypes
) {

    fun create(type: Type?, modules: HlaModules): ViewType {
        if (type == null) {
            return BaseViewType(BaseType.VOID, languageTypes)
        }

        val simpleVO = modules.findSimpleVO(type)
        val complexVO = modules.findComplexVO(type)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        val isBaseType = BaseType.isBaseType(type.name)

        return when {
            isList -> ListViewType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST), modules), languageTypes)
            simpleVO != null -> SimpleVOViewType(type.name, BaseViewType(BaseType.of(simpleVO.typeName), languageTypes))
            complexVO != null -> ComplexVOViewType(type.name)
            isBaseType -> BaseViewType(BaseType.of(type.name), languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}