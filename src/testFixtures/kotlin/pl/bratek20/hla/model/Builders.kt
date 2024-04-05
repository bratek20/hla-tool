package pl.bratek20.hla.model

data class HlaModuleDef(
    var name: String = "test",
)
fun hlaModule(ov: HlaModuleDef.() -> Unit): HlaModule {
    val def = HlaModuleDef().apply(ov)
    return HlaModule(
        name = def.name,
        valueObjects = emptyList(),
        interfaces = emptyList()
    )
}