package pl.bratek20.somemodule.fixtures

import pl.bratek20.somemodule.api.*

data class SomeClassDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someClass(init: SomeClassDef.() -> Unit = {}): SomeClass {
    val def = SomeClassDef().apply(init)
    return SomeClass(
        id = SomeId(def.id),
        amount = def.amount,
    )
}