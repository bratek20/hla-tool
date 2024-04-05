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

data class ValueObjectDef(
    var name: String = "test",
    var fields: List<FieldDef.() -> Unit> = listOf(),
)
fun valueObject(ov: ValueObjectDef.() -> Unit): ValueObject {
    val def = ValueObjectDef().apply(ov)
    return ValueObject(
        name = def.name,
        fields = def.fields.map { field(it) }
    )
}

data class HlaModuleDef(
    var name: String = "test",
    var valueObjects: List<ValueObjectDef.()->Unit> = listOf(),
)
fun hlaModule(ov: HlaModuleDef.() -> Unit): HlaModule {
    val def = HlaModuleDef().apply(ov)
    return HlaModule(
        name = def.name,
        valueObjects = def.valueObjects.map { valueObject(it) },
        interfaces = emptyList()
    )
}