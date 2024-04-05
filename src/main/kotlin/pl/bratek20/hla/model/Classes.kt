package pl.bratek20.hla.model

data class HlaModule(
    val name: String,
    val valueObjects: List<ValueObject>,
    val interfaces: List<Interface>
)

data class Field(
    val name: String,
    val type: String
)

data class ValueObject(
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
    val returnType: String,
    val args: List<Argument>,
    val throws: List<Exception>
)