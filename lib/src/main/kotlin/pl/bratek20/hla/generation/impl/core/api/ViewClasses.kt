package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.utils.camelToPascalCase

data class FieldView(
    val name: String,
    val type: ViewType
)
data class ComplexValueObjectView(
    val name: String,
    val fields: List<FieldView>
)
data class ComplexCustomTypeView(
    val name: String,
    val fields: List<FieldView>
) {
    fun getterName(fieldName: String): String {
        return "get$name${camelToPascalCase(fieldName)}"
    }
}
data class SimpleValueObjectView(
    val name: String,
    val type: String
)
data class ValueObjectsView(
    val simpleList: List<SimpleValueObjectView>,
    val complexList: List<ComplexValueObjectView>
)

data class CustomTypesView(
    val simpleList: List<SimpleValueObjectView>,
    val complexList: List<ComplexCustomTypeView>
)

data class ArgumentView(
    val name: String,
    val type: String
)
data class MethodView(
    val name: String,
    val returnType: String?,
    val args: List<ArgumentView>,
    val throws: List<String>,
)
data class InterfaceView(
    val name: String,
    val methods: List<MethodView>
)
