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

    fun create(type: Type?, module: HlaModule): ViewType {
        if (type == null) {
            return BaseViewType(BaseType.VOID, languageTypes)
        }

        val simpleVO = findSimpleVO(type, module)
        val complexVO = findComplexVO(type, module)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        return when {
            isList -> ListViewType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST), module), languageTypes)
            simpleVO != null -> SimpleVOViewType(type.name, BaseViewType(BaseType.of(simpleVO.typeName), languageTypes))
            complexVO != null -> ComplexVOViewType(type.name)
            else -> BaseViewType(BaseType.of(type.name), languageTypes)
        }
    }


    private fun findSimpleVO(type: Type, module: HlaModule): SimpleValueObject? {
        return module.simpleValueObjects.find { it.name == type.name }
    }

    private fun findComplexVO(type: Type, module: HlaModule): ComplexValueObject? {
        return module.complexValueObjects.find { it.name == type.name }
    }
}