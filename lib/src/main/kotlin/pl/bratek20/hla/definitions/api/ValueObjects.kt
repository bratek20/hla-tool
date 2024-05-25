package pl.bratek20.hla.definitions.api

import pl.bratek20.hla.facade.api.*

data class PropertyKey(
    val name: String,
    val type: TypeDefinition,
)

data class EnumDefinition(
    val name: String,
    val values: List<String>,
)

data class ModuleDefinition(
    val name: ModuleName,
    val namedTypes: List<SimpleStructureDefinition>,
    val valueObjects: List<ComplexStructureDefinition>,
    val interfaces: List<InterfaceDefinition>,
    val properties: List<ComplexStructureDefinition>,
    val propertyKeys: List<PropertyKey>,
    val enums: List<EnumDefinition>,
    val simpleCustomTypes: List<SimpleStructureDefinition>,
    val complexCustomTypes: List<ComplexStructureDefinition>,
)

data class TypeDefinition(
    val name: String,
    val wrappers: List<TypeWrapper>,
)

data class FieldDefinition(
    val name: String,
    val type: TypeDefinition,
)

data class Attribute(
    val name: String,
    val value: String,
)

data class SimpleStructureDefinition(
    val name: String,
    val typeName: String,
    val attributes: List<Attribute>,
)

data class ComplexStructureDefinition(
    val name: String,
    val fields: List<FieldDefinition>,
)

data class InterfaceDefinition(
    val name: String,
    val methods: List<MethodDefinition>,
)

data class ArgumentDefinition(
    val name: String,
    val type: TypeDefinition,
)

data class ExceptionDefinition(
    val name: String,
)

data class MethodDefinition(
    val name: String,
    val returnType: TypeDefinition,
    val args: List<ArgumentDefinition>,
    val throws: List<ExceptionDefinition>,
)