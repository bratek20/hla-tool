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