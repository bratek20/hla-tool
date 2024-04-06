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

data class HlaModuleDef(
    var name: String = "test",
    var simpleValueObjects: List<SimpleValueObjectDef.()->Unit> = listOf(),
    var complexValueObjects: List<ComplexValueObjectDef.()->Unit> = listOf(),
)
fun hlaModule(ov: HlaModuleDef.() -> Unit): HlaModule {
    val def = HlaModuleDef().apply(ov)
    return HlaModule(
        name = def.name,
        simpleValueObjects = def.simpleValueObjects.map { simpleValueObject(it) },
        complexValueObjects = def.complexValueObjects.map { complexValueObject(it) },
        interfaces = emptyList()
    )
}