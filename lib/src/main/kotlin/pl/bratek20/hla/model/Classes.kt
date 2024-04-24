package pl.bratek20.hla.model

import pl.bratek20.hla.generation.api.ModuleName

enum class BaseType {
    STRING,
    INT,
    BOOL,
    VOID;

    companion object {
        fun of(value: String): BaseType {
            return BaseType.valueOf(value.uppercase())
        }

        fun isBaseType(value: String): Boolean {
            return entries.any { it.name == value.uppercase() }
        }
    }
}

data class HlaModule(
    val name: ModuleName,
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
    val typeName: String
) {
    fun type(): Type {
        return Type(name = typeName)
    }
}

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
    val type: Type
)

data class Exception(
    val name: String
)

data class Method(
    val name: String,
    val returnType: Type?,
    val args: List<Argument>,
    val throws: List<Exception> = emptyList()
)