package pl.bratek20.hla.model

enum class BuiltInType {
    STRING,
    INT,
    BOOL,
}

data class HlaModule(
    val name: String,
    val simpleValueObjects: List<SimpleValueObject>,
    val complexValueObjects: List<ComplexValueObject>,
    val interfaces: List<Interface>
)

enum class TypeWrapper {
    LIST,
    OPTIONAL
}

data class Type(
    val name: String,
    val wrappers: List<TypeWrapper> = emptyList()
)
data class Field(
    val name: String,
    val type: Type
)

data class SimpleValueObject(
    val name: String,
    val type: String
)

data class ComplexValueObject(
    val name: String,
    val fields: List<Field>
)

data class Interface(
    val name: String,
    val methods: List<Method>
)

data class Argument(
    val name: String,
    val type: String
)

data class Exception(
    val name: String
)

data class Method(
    val name: String,
    val returnType: String?,
    val args: List<Argument>,
    val throws: List<Exception> = emptyList()
)