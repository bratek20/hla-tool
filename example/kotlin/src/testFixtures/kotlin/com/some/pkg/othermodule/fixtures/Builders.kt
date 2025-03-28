// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.othermodule.fixtures

import com.some.pkg.othermodule.api.*

fun otherId(value: Int = 0): OtherId {
    return OtherId(value)
}

data class OtherPropertyDef(
    var id: Int = 0,
    var name: String = "someValue",
)
fun otherProperty(init: OtherPropertyDef.() -> Unit = {}): OtherProperty {
    val def = OtherPropertyDef().apply(init)
    return OtherProperty.create(
        id = OtherId(def.id),
        name = def.name,
    )
}

data class OtherClassDef(
    var id: Int = 0,
    var amount: Int = 0,
)
fun otherClass(init: OtherClassDef.() -> Unit = {}): OtherClass {
    val def = OtherClassDef().apply(init)
    return OtherClass.create(
        id = OtherId(def.id),
        amount = def.amount,
    )
}

data class OtherClassWIthUniqueIdDef(
    var uniqueId: String = "someValue",
)
fun otherClassWIthUniqueId(init: OtherClassWIthUniqueIdDef.() -> Unit = {}): OtherClassWIthUniqueId {
    val def = OtherClassWIthUniqueIdDef().apply(init)
    return OtherClassWIthUniqueId.create(
        uniqueId = def.uniqueId,
    )
}

data class OtherDataDef(
    var id: Int = 0,
)
fun otherData(init: OtherDataDef.() -> Unit = {}): OtherData {
    val def = OtherDataDef().apply(init)
    return OtherData.create(
        id = OtherId(def.id),
    )
}