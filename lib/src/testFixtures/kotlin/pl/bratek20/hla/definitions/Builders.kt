package pl.bratek20.hla.definitions

import pl.bratek20.hla.definitions.api.*

data class TypeDef(
    var name: String = "test",
    var wrappers: List<TypeWrapper> = emptyList(),
)
fun type(ov: TypeDef.() -> Unit): TypeDefinition {
    val def = TypeDef().apply(ov)
    return TypeDefinition(
        name = def.name,
        wrappers = def.wrappers
    )
}

data class FieldDef(
    var name: String = "test",
    var type: TypeDef.() -> Unit = {},
)
fun field(ov: FieldDef.() -> Unit): FieldDefinition {
    val def = FieldDef().apply(ov)
    return FieldDefinition(
        name = def.name,
        type = type(def.type)
    )
}

data class SimpleValueObjectDef(
    var name: String = "test",
    var type: String = "String",
)
fun simpleValueObject(ov: SimpleValueObjectDef.() -> Unit): SimpleStructureDefinition {
    val def = SimpleValueObjectDef().apply(ov)
    return SimpleStructureDefinition(
        name = def.name,
        typeName = def.type
    )
}

data class ComplexValueObjectDef(
    var name: String = "test",
    var fields: List<FieldDef.() -> Unit> = listOf(),
)
fun complexValueObject(ov: ComplexValueObjectDef.() -> Unit): ComplexStructureDefinition {
    val def = ComplexValueObjectDef().apply(ov)
    return ComplexStructureDefinition(
        name = def.name,
        fields = def.fields.map { field(it) }
    )
}

data class ArgumentDef(
    var name: String = "test",
    var type: TypeDef.() -> Unit = {},
)
fun argument(ov: ArgumentDef.() -> Unit): ArgumentDefinition {
    val def = ArgumentDef().apply(ov)
    return ArgumentDefinition(
        name = def.name,
        type = type(def.type)
    )
}

data class ExceptionDef(
    var name: String = "test",
)
fun exception(ov: ExceptionDef.() -> Unit): ExceptionDefinition {
    val def = ExceptionDef().apply(ov)
    return ExceptionDefinition(
        name = def.name
    )
}

data class MethodDef(
    var name: String = "test",
    var returnType: (TypeDef.() -> Unit)? = null,
    var args: List<ArgumentDef.()->Unit> = listOf(),
    var throws: List<ExceptionDef.()->Unit> = listOf(),
)
fun method(ov: MethodDef.() -> Unit): MethodDefinition {
    val def = MethodDef().apply(ov)
    return MethodDefinition(
        name = def.name,
        returnType = def.returnType?.let { type(it) },
        args = def.args.map { argument(it) },
        throws = def.throws.map { exception(it) }
    )
}

data class InterfaceDef(
    var name: String = "test",
    var methods: List<MethodDef.()->Unit> = listOf(),
)
fun interfaceDef(ov: InterfaceDef.() -> Unit): InterfaceDefinition {
    val def = InterfaceDef().apply(ov)
    return InterfaceDefinition(
        name = def.name,
        methods = def.methods.map { method(it) }
    )
}

data class HlaModuleDef(
    var name: String = "test",
    var simpleValueObjects: List<SimpleValueObjectDef.()->Unit> = listOf(),
    var complexValueObjects: List<ComplexValueObjectDef.()->Unit> = listOf(),
    var interfaces: List<InterfaceDef.()->Unit> = listOf(),
)
fun moduleDefinition(ov: HlaModuleDef.() -> Unit): ModuleDefinition {
    val def = HlaModuleDef().apply(ov)
    return ModuleDefinition(
        name = ModuleName(def.name),
        simpleValueObjects = def.simpleValueObjects.map { simpleValueObject(it) },
        complexValueObjects = def.complexValueObjects.map { complexValueObject(it) },
        interfaces = def.interfaces.map { interfaceDef(it) },
        propertyValueObjects = emptyList(),
        propertyMappings = emptyList(),
        enums = emptyList()
    )
}