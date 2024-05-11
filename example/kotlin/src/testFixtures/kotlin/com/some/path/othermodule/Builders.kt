package com.some.path.othermodule

import com.some.path.othermodule.api.OtherClass
import com.some.path.othermodule.api.OtherId
import pl.bratek20.othermodule.api.*

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