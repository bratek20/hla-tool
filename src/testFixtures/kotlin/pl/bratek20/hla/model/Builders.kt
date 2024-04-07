package pl.bratek20.hla.model

data class FieldDef(
    var name: String = "test",
    var type: String = "String",
)
fun field(ov: FieldDef.() -> Unit): Field {
    val def = FieldDef().apply(ov)
    return Field(
        name = def.name,
        type = def.type
    )
}

data class SimpleValueObjectDef(
    var name: String = "test",
    var type: String = "String",
)
fun simpleValueObject(ov: SimpleValueObjectDef.() -> Unit): SimpleValueObject {
    val def = SimpleValueObjectDef().apply(ov)
    return SimpleValueObject(
        name = def.name,
        type = def.type
    )
}

data class ComplexValueObjectDef(
    var name: String = "test",
    var fields: List<FieldDef.() -> Unit> = listOf(),
)
fun complexValueObject(ov: ComplexValueObjectDef.() -> Unit): ComplexValueObject {
    val def = ComplexValueObjectDef().apply(ov)
    return ComplexValueObject(
        name = def.name,
        fields = def.fields.map { field(it) }
    )
}

data class ArgumentDef(
    var name: String = "test",
    var type: String = "String",
)
fun argument(ov: ArgumentDef.() -> Unit): Argument {
    val def = ArgumentDef().apply(ov)
    return Argument(
        name = def.name,
        type = def.type
    )
}

data class ExceptionDef(
    var name: String = "test",
)
fun exception(ov: ExceptionDef.() -> Unit): Exception {
    val def = ExceptionDef().apply(ov)
    return Exception(
        name = def.name
    )
}

data class MethodDef(
    var name: String = "test",
    var returnType: String? = null,
    var args: List<ArgumentDef.()->Unit> = listOf(),
    var throws: List<ExceptionDef.()->Unit> = listOf(),
)
fun method(ov: MethodDef.() -> Unit): Method {
    val def = MethodDef().apply(ov)
    return Method(
        name = def.name,
        returnType = def.returnType,
        args = def.args.map { argument(it) },
        throws = def.throws.map { exception(it) }
    )
}

data class InterfaceDef(
    var name: String = "test",
    var methods: List<MethodDef.()->Unit> = listOf(),
)
fun interfaceDef(ov: InterfaceDef.() -> Unit): Interface {
    val def = InterfaceDef().apply(ov)
    return Interface(
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
fun hlaModule(ov: HlaModuleDef.() -> Unit): HlaModule {
    val def = HlaModuleDef().apply(ov)
    return HlaModule(
        name = def.name,
        simpleValueObjects = def.simpleValueObjects.map { simpleValueObject(it) },
        complexValueObjects = def.complexValueObjects.map { complexValueObject(it) },
        interfaces = def.interfaces.map { interfaceDef(it) }
    )
}