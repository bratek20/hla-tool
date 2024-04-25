package pl.bratek20.hla.definitions

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

data class ModuleDefinition(
    val name: ModuleName,
    val simpleValueObjects: List<SimpleStructureDefinition>,
    val complexValueObjects: List<ComplexStructureDefinition>,
    val interfaces: List<InterfaceDefinition>,
    val propertyValueObjects: List<ComplexStructureDefinition>
)

enum class TypeWrapper {
    LIST,
    OPTIONAL
}

data class TypeDefinition(
    val name: String,
    val wrappers: List<TypeWrapper> = emptyList()
)
data class FieldDefinition(
    val name: String,
    val type: TypeDefinition
)

data class SimpleStructureDefinition(
    val name: String,
    val typeName: String
) {
    fun type(): TypeDefinition {
        return TypeDefinition(name = typeName)
    }
}

data class ComplexStructureDefinition(
    val name: String,
    val fields: List<FieldDefinition>
)

data class InterfaceDefinition(
    val name: String,
    val methods: List<MethodDefinition>
)

data class ArgumentDefinition(
    val name: String,
    val type: TypeDefinition
)

data class Exception(
    val name: String
)

data class MethodDefinition(
    val name: String,
    val returnType: TypeDefinition?,
    val args: List<ArgumentDefinition>,
    val throws: List<Exception> = emptyList()
)