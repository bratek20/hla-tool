package pl.bratek20.example.fixtures

import pl.bratek20.example.api.*

data class SomeClassDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someClass(init: SomeClassDef.() -> Unit = {}): SomeClass {
    val def = SomeClassDef().apply(init)
    return SomeClass(
        id = def.id,
        amount = def.amount,
    )
}

data class SomeClass2Def(
    var id: String = "someValue",
    var enabled: Boolean = false,
)
fun someClass2(init: SomeClass2Def.() -> Unit = {}): SomeClass2 {
    val def = SomeClass2Def().apply(init)
    return SomeClass2(
        id = def.id,
        enabled = def.enabled,
    )
}
