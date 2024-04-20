package pl.bratek20.somemodule.fixtures

import pl.bratek20.othermodule.api.*
import pl.bratek20.othermodule.fixtures.*

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

data class SomeClass2Def(
    var id: String = "someValue",
    var enabled: Boolean = false,
    var names: List<String> = emptyList(),
    var ids: List<String> = emptyList(),
)
fun someClass2(init: SomeClass2Def.() -> Unit = {}): SomeClass2 {
    val def = SomeClass2Def().apply(init)
    return SomeClass2(
        id = SomeId(def.id),
        enabled = def.enabled,
        names = def.names,
        ids = def.ids.map { it -> SomeId(it) },
    )
}

data class SomeClass3Def(
    var class2Object: (SomeClass2Def.() -> Unit) = {},
    var class2List: List<(SomeClass2Def.() -> Unit)> = emptyList(),
)
fun someClass3(init: SomeClass3Def.() -> Unit = {}): SomeClass3 {
    val def = SomeClass3Def().apply(init)
    return SomeClass3(
        class2Object = someClass2(def.class2Object),
        class2List = def.class2List.map { it -> someClass2(it) },
    )
}

data class SomeClass4Def(
    var otherId: String = "someValue",
    var otherClass: (OtherClassDef.() -> Unit) = {},
    var otherIdList: List<String> = emptyList(),
    var otherClassList: List<(OtherClassDef.() -> Unit)> = emptyList(),
)
fun someClass4(init: SomeClass4Def.() -> Unit = {}): SomeClass4 {
    val def = SomeClass4Def().apply(init)
    return SomeClass4(
        otherId = OtherId(def.otherId),
        otherClass = otherClass(def.otherClass),
        otherIdList = def.otherIdList.map { it -> OtherId(it) },
        otherClassList = def.otherClassList.map { it -> otherClass(it) },
    )
}