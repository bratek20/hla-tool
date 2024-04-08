package pl.bratek20.hla.model

data class HlaModule(
    val name: String,
    val simpleValueObjects: List<SimpleValueObject>,
    val complexValueObjects: List<ComplexValueObject>,
    val interfaces: List<Interface>
) {
    fun findSimpleVO(type: String): SimpleValueObject? {
        return simpleValueObjects.find { it.name == type }
    }
}

data class Field(
    val name: String,
    val type: String
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
    val throws: List<Exception>
)