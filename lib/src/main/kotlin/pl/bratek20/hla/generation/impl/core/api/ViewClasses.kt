package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

data class FieldView(
    val name: String,
    val type: ViewType
)
data class ComplexValueObjectView(
    val name: String,
    val fields: List<FieldView>
)

data class SimpleCustomTypeView(
    val name: String,
    val type: String
) {
    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"
    }

    fun getterName(): String {
        return "${pascalToCamelCase(name)}GetValue"
    }
}

data class ComplexCustomTypeView(
    val name: String,
    val fields: List<FieldView>
) {
    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"
    }

    fun getterName(fieldName: String): String {
        return "${pascalToCamelCase(name)}Get${camelToPascalCase(fieldName)}" //TODO code duplication with language types
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
    val simpleList: List<SimpleCustomTypeView>,
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
