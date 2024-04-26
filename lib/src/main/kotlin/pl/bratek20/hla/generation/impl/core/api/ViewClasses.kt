package pl.bratek20.hla.generation.impl.core.api

data class FieldView(
    val name: String,
    val type: ViewType
)
data class ComplexValueObjectView(
    val name: String,
    val fields: List<FieldView>
)
data class SimpleValueObjectView(
    val name: String,
    val type: String
)
data class ValueObjectsView(
    val simpleList: List<SimpleValueObjectView>,
    val complexList: List<ComplexValueObjectView>
)

data class ArgumentView(
    val name: String,
    val type: String
)
data class MethodView(
    val name: String,
    val returnType: String?,
    val args: List<ArgumentView>
)
data class InterfaceView(
    val name: String,
    val methods: List<MethodView>
)
