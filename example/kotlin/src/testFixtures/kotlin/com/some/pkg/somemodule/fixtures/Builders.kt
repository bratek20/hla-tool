// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.fixtures

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*
import com.some.pkg.typesmodule.api.*
import com.some.pkg.typesmodule.fixtures.*

import com.some.pkg.somemodule.api.*

fun someId(value: String = "someValue"): SomeId {
    return SomeId(value)
}

fun someIntWrapper(value: Int = 0): SomeIntWrapper {
    return SomeIntWrapper(value)
}

fun someId2(value: Int = 0): SomeId2 {
    return SomeId2(value)
}

data class SomeClassDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someClass(init: SomeClassDef.() -> Unit = {}): SomeClass {
    val def = SomeClassDef().apply(init)
    return SomeClass.create(
        id = SomeId(def.id),
        amount = def.amount,
    )
}

data class SomeClass2Def(
    var id: String = "someValue",
    var names: List<String> = emptyList(),
    var ids: List<String> = emptyList(),
    var enabled: Boolean = true,
)
fun someClass2(init: SomeClass2Def.() -> Unit = {}): SomeClass2 {
    val def = SomeClass2Def().apply(init)
    return SomeClass2.create(
        id = SomeId(def.id),
        names = def.names,
        ids = def.ids.map { it -> SomeId(it) },
        enabled = def.enabled,
    )
}

data class SomeClass3Def(
    var class2Object: (SomeClass2Def.() -> Unit) = {},
    var someEnum: String = SomeEnum.VALUE_A.name,
    var class2List: List<(SomeClass2Def.() -> Unit)> = emptyList(),
)
fun someClass3(init: SomeClass3Def.() -> Unit = {}): SomeClass3 {
    val def = SomeClass3Def().apply(init)
    return SomeClass3.create(
        class2Object = someClass2(def.class2Object),
        someEnum = SomeEnum.valueOf(def.someEnum),
        class2List = def.class2List.map { it -> someClass2(it) },
    )
}

data class SomeClass4Def(
    var otherId: Int = 0,
    var otherClass: (OtherClassDef.() -> Unit) = {},
    var otherIdList: List<Int> = emptyList(),
    var otherClassList: List<(OtherClassDef.() -> Unit)> = emptyList(),
)
fun someClass4(init: SomeClass4Def.() -> Unit = {}): SomeClass4 {
    val def = SomeClass4Def().apply(init)
    return SomeClass4.create(
        otherId = OtherId(def.otherId),
        otherClass = otherClass(def.otherClass),
        otherIdList = def.otherIdList.map { it -> OtherId(it) },
        otherClassList = def.otherClassList.map { it -> otherClass(it) },
    )
}

data class SomeClass5Def(
    var date: String = "01/01/1970 00:00",
    var dateRange: (DateRangeDef.() -> Unit) = {},
    var dateRangeWrapper: (DateRangeWrapperDef.() -> Unit) = {},
    var someProperty: (SomePropertyDef.() -> Unit) = {},
    var otherProperty: (OtherPropertyDef.() -> Unit) = {},
)
fun someClass5(init: SomeClass5Def.() -> Unit = {}): SomeClass5 {
    val def = SomeClass5Def().apply(init)
    return SomeClass5.create(
        date = dateCreate(def.date),
        dateRange = dateRange(def.dateRange),
        dateRangeWrapper = dateRangeWrapper(def.dateRangeWrapper),
        someProperty = someProperty(def.someProperty),
        otherProperty = otherProperty(def.otherProperty),
    )
}

data class SomeClass6Def(
    var someClassOpt: (SomeClassDef.() -> Unit)? = null,
    var optString: String? = null,
    var sameClassList: List<(SomeClass6Def.() -> Unit)> = emptyList(),
)
fun someClass6(init: SomeClass6Def.() -> Unit = {}): SomeClass6 {
    val def = SomeClass6Def().apply(init)
    return SomeClass6.create(
        someClassOpt = def.someClassOpt?.let { it -> someClass(it) },
        optString = def.optString,
        sameClassList = def.sameClassList.map { it -> someClass6(it) },
    )
}

data class RecordClassDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun recordClass(init: RecordClassDef.() -> Unit = {}): RecordClass {
    val def = RecordClassDef().apply(init)
    return RecordClass.create(
        id = SomeId(def.id),
        amount = def.amount,
    )
}

data class SomeQueryInputDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someQueryInput(init: SomeQueryInputDef.() -> Unit = {}): SomeQueryInput {
    val def = SomeQueryInputDef().apply(init)
    return SomeQueryInput.create(
        id = SomeId(def.id),
        amount = def.amount,
    )
}

data class SomePropertyDef(
    var other: (OtherPropertyDef.() -> Unit) = {},
    var id2: Int? = null,
    var range: (DateRangeDef.() -> Unit)? = null,
    var doubleExample: Double = 0.0,
    var longExample: Long = 0L,
    var goodName: String = "someValue",
    var customData: com.github.bratek20.architecture.serialization.api.Struct = com.github.bratek20.architecture.serialization.api.Struct(),
)
fun someProperty(init: SomePropertyDef.() -> Unit = {}): SomeProperty {
    val def = SomePropertyDef().apply(init)
    return SomeProperty.create(
        other = otherProperty(def.other),
        id2 = def.id2?.let { it -> SomeId2(it) },
        range = def.range?.let { it -> dateRange(it) },
        doubleExample = def.doubleExample,
        longExample = def.longExample,
        goodName = def.goodName,
        customData = def.customData,
    )
}

data class SomeProperty2Def(
    var value: String = "someValue",
    var custom: Any = Any(),
    var someEnum: String = SomeEnum.VALUE_A.name,
    var customOpt: Any? = null,
)
fun someProperty2(init: SomeProperty2Def.() -> Unit = {}): SomeProperty2 {
    val def = SomeProperty2Def().apply(init)
    return SomeProperty2.create(
        value = def.value,
        custom = def.custom,
        someEnum = SomeEnum.valueOf(def.someEnum),
        customOpt = def.customOpt,
    )
}

data class DateRangeWrapperDef(
    var range: (DateRangeDef.() -> Unit) = {},
)
fun dateRangeWrapper(init: DateRangeWrapperDef.() -> Unit = {}): DateRangeWrapper {
    val def = DateRangeWrapperDef().apply(init)
    return dateRangeWrapperCreate(
        range = dateRange(def.range),
    )
}

data class SomeDataDef(
    var other: (OtherDataDef.() -> Unit) = {},
    var custom: Any = Any(),
    var customOpt: Any? = null,
    var goodDataName: String = "someValue",
)
fun someData(init: SomeDataDef.() -> Unit = {}): SomeData {
    val def = SomeDataDef().apply(init)
    return SomeData.create(
        other = otherData(def.other),
        custom = def.custom,
        customOpt = def.customOpt,
        goodDataName = def.goodDataName,
    )
}

data class SomeData2Def(
    var optEnum: String? = null,
    var optCustomType: String? = null,
)
fun someData2(init: SomeData2Def.() -> Unit = {}): SomeData2 {
    val def = SomeData2Def().apply(init)
    return SomeData2.create(
        optEnum = def.optEnum?.let { it -> SomeEnum.valueOf(it) },
        optCustomType = def.optCustomType?.let { it -> dateCreate(it) },
    )
}
fun legacyType(value: com.some.pkg.legacy.LegacyType?): com.some.pkg.legacy.LegacyType {
    return value!!
}
