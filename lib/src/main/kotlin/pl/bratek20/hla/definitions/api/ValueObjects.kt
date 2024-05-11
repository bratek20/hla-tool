package pl.bratek20.hla.definitions.api

data class PropertyMapping(
    val key: String,
    val type: TypeDefinition
)

data class EnumDefinition(
    val name: String,
    val values: List<String>
)

data class ModuleName(val value: String)

data class ModuleDefinition(
    val name: ModuleName,
    val simpleValueObjects: List<SimpleStructureDefinition>,
    val complexValueObjects: List<ComplexStructureDefinition>,
    val interfaces: List<InterfaceDefinition>,
    val propertyValueObjects: List<ComplexStructureDefinition>,
    val propertyMappings: List<PropertyMapping>,
    val enums: List<EnumDefinition>,
)

data class TypeDefinition(
    val name: String,
    val wrappers: List<TypeWrapper>
)

data class FieldDefinition(
    val name: String,
    val type: TypeDefinition
)

data class SimpleStructureDefinition(
    val name: String,
    val typeName: String
)

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

data class ExceptionDefinition(
    val name: String
)

data class MethodDefinition(
    val name: String,
    val returnType: TypeDefinition?,
    val args: List<ArgumentDefinition>,
    val throws: List<ExceptionDefinition>
)