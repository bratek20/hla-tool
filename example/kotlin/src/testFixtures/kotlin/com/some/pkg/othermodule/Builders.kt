package com.some.pkg.othermodule

import com.some.pkg.othermodule.api.OtherClass
import com.some.pkg.othermodule.api.OtherId

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