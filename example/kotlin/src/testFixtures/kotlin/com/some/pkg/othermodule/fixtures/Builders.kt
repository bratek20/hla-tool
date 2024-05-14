package com.some.pkg.othermodule.fixtures

import com.some.pkg.othermodule.api.*

data class OtherClassDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun otherClass(init: OtherClassDef.() -> Unit = {}): OtherClass {
    val def = OtherClassDef().apply(init)
    return OtherClass(
        id = OtherId(def.id),
        amount = def.amount,
    )
}

data class OtherPropertyDef(
    var id: String = "someValue",
    var name: String = "someValue",
)
fun otherProperty(init: OtherPropertyDef.() -> Unit = {}): OtherProperty {
    val def = OtherPropertyDef().apply(init)
    return OtherProperty(
        id = def.id,
        name = def.name,
    )
}