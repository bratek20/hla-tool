// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.fixtures

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*
import com.some.pkg.simplemodule.api.*
import com.some.pkg.simplemodule.fixtures.*
import com.some.pkg.typesmodule.api.*
import com.some.pkg.typesmodule.fixtures.*

import com.some.pkg.somemodule.api.*

fun someId(value: String = "someValue"): SomeId {
    return SomeId(value)
}

fun someIntWrapper(value: Int = 5): SomeIntWrapper {
    return SomeIntWrapper(value)
}

fun someId2(value: Int = 0): SomeId2 {
    return SomeId2(value)
}

data class SomeClassDef(
    var id: String = "someValue",
    var amount: Int = 10,
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
    var class2List: List<(SomeClass2Def.() -> Unit)> = emptyList(),
    var sameClassList: List<(SomeClass6Def.() -> Unit)> = emptyList(),
)
fun someClass6(init: SomeClass6Def.() -> Unit = {}): SomeClass6 {
    val def = SomeClass6Def().apply(init)
    return SomeClass6.create(
        someClassOpt = def.someClassOpt?.let { it -> someClass(it) },
        optString = def.optString,
        class2List = def.class2List.map { it -> someClass2(it) },
        sameClassList = def.sameClassList.map { it -> someClass6(it) },
    )
}

data class ClassHavingOptListDef(
    var optList: List<(SomeClassDef.() -> Unit)>? = null,
)
fun classHavingOptList(init: ClassHavingOptListDef.() -> Unit = {}): ClassHavingOptList {
    val def = ClassHavingOptListDef().apply(init)
    return ClassHavingOptList.create(
        optList = def.optList?.let { it -> it.map { it -> someClass(it) } },
    )
}

data class ClassHavingOptSimpleVoDef(
    var optSimpleVo: String? = null,
)
fun classHavingOptSimpleVo(init: ClassHavingOptSimpleVoDef.() -> Unit = {}): ClassHavingOptSimpleVo {
    val def = ClassHavingOptSimpleVoDef().apply(init)
    return ClassHavingOptSimpleVo.create(
        optSimpleVo = def.optSimpleVo?.let { it -> SomeId(it) },
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

data class ClassWithOptExamplesDef(
    var optInt: Int? = 1,
    var optIntWrapper: Int? = 2,
)
fun classWithOptExamples(init: ClassWithOptExamplesDef.() -> Unit = {}): ClassWithOptExamples {
    val def = ClassWithOptExamplesDef().apply(init)
    return ClassWithOptExamples.create(
        optInt = def.optInt,
        optIntWrapper = def.optIntWrapper?.let { it -> SomeIntWrapper(it) },
    )
}

data class ClassWithEnumListDef(
    var enumList: List<String> = emptyList(),
)
fun classWithEnumList(init: ClassWithEnumListDef.() -> Unit = {}): ClassWithEnumList {
    val def = ClassWithEnumListDef().apply(init)
    return ClassWithEnumList.create(
        enumList = def.enumList.map { it -> SomeEnum2.valueOf(it) },
    )
}

data class ClassWithBoolFieldDef(
    var boolField: Boolean = false,
)
fun classWithBoolField(init: ClassWithBoolFieldDef.() -> Unit = {}): ClassWithBoolField {
    val def = ClassWithBoolFieldDef().apply(init)
    return ClassWithBoolField.create(
        boolField = def.boolField,
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

data class SomeHandlerInputDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someHandlerInput(init: SomeHandlerInputDef.() -> Unit = {}): SomeHandlerInput {
    val def = SomeHandlerInputDef().apply(init)
    return SomeHandlerInput.create(
        id = SomeId(def.id),
        amount = def.amount,
    )
}

data class SomeHandlerOutputDef(
    var id: String = "someValue",
    var amount: Int = 0,
)
fun someHandlerOutput(init: SomeHandlerOutputDef.() -> Unit = {}): SomeHandlerOutput {
    val def = SomeHandlerOutputDef().apply(init)
    return SomeHandlerOutput.create(
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
    var customData: com.github.bratek20.architecture.structs.api.Struct = com.github.bratek20.architecture.structs.api.Struct(),
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

data class SomePropertyEntryDef(
    var id: String = "someValue",
)
fun somePropertyEntry(init: SomePropertyEntryDef.() -> Unit = {}): SomePropertyEntry {
    val def = SomePropertyEntryDef().apply(init)
    return SomePropertyEntry.create(
        id = SomeId(def.id),
    )
}

data class SomeReferencingPropertyDef(
    var referenceId: String = "someValue",
)
fun someReferencingProperty(init: SomeReferencingPropertyDef.() -> Unit = {}): SomeReferencingProperty {
    val def = SomeReferencingPropertyDef().apply(init)
    return SomeReferencingProperty.create(
        referenceId = SomeId(def.referenceId),
    )
}

data class SomeReferencingPropertyFieldListDef(
    var referenceIdList: List<String> = emptyList(),
)
fun someReferencingPropertyFieldList(init: SomeReferencingPropertyFieldListDef.() -> Unit = {}): SomeReferencingPropertyFieldList {
    val def = SomeReferencingPropertyFieldListDef().apply(init)
    return SomeReferencingPropertyFieldList.create(
        referenceIdList = def.referenceIdList.map { it -> SomeId(it) },
    )
}

data class SomeStructureWithUniqueIdsDef(
    var entries: List<(UniqueIdEntryDef.() -> Unit)> = emptyList(),
)
fun someStructureWithUniqueIds(init: SomeStructureWithUniqueIdsDef.() -> Unit = {}): SomeStructureWithUniqueIds {
    val def = SomeStructureWithUniqueIdsDef().apply(init)
    return SomeStructureWithUniqueIds.create(
        entries = def.entries.map { it -> uniqueIdEntry(it) },
    )
}

data class NestedUniqueIdsDef(
    var entries: List<(UniqueIdEntryDef.() -> Unit)> = emptyList(),
)
fun nestedUniqueIds(init: NestedUniqueIdsDef.() -> Unit = {}): NestedUniqueIds {
    val def = NestedUniqueIdsDef().apply(init)
    return NestedUniqueIds.create(
        entries = def.entries.map { it -> uniqueIdEntry(it) },
    )
}

data class SomeStructureWithUniqueNestedIdsDef(
    var nestedUniqueIds: List<(NestedUniqueIdsDef.() -> Unit)> = emptyList(),
)
fun someStructureWithUniqueNestedIds(init: SomeStructureWithUniqueNestedIdsDef.() -> Unit = {}): SomeStructureWithUniqueNestedIds {
    val def = SomeStructureWithUniqueNestedIdsDef().apply(init)
    return SomeStructureWithUniqueNestedIds.create(
        nestedUniqueIds = def.nestedUniqueIds.map { it -> nestedUniqueIds(it) },
    )
}

data class SomeStructureWithMultipleUniqueNestedIdsDef(
    var moreNestedFields: List<(SomeStructureWithUniqueNestedIdsDef.() -> Unit)> = emptyList(),
)
fun someStructureWithMultipleUniqueNestedIds(init: SomeStructureWithMultipleUniqueNestedIdsDef.() -> Unit = {}): SomeStructureWithMultipleUniqueNestedIds {
    val def = SomeStructureWithMultipleUniqueNestedIdsDef().apply(init)
    return SomeStructureWithMultipleUniqueNestedIds.create(
        moreNestedFields = def.moreNestedFields.map { it -> someStructureWithUniqueNestedIds(it) },
    )
}

data class SomeClassWIthOtherClassUniqueIdsDef(
    var otherClass: (OtherClassWIthUniqueIdDef.() -> Unit) = {},
)
fun someClassWIthOtherClassUniqueIds(init: SomeClassWIthOtherClassUniqueIdsDef.() -> Unit = {}): SomeClassWIthOtherClassUniqueIds {
    val def = SomeClassWIthOtherClassUniqueIdsDef().apply(init)
    return SomeClassWIthOtherClassUniqueIds.create(
        otherClass = otherClassWIthUniqueId(def.otherClass),
    )
}

data class SomeStructWithNestedOtherClassUniqueIdsDef(
    var someNestedWithUniqueIds: List<(SomeClassWIthOtherClassUniqueIdsDef.() -> Unit)> = emptyList(),
)
fun someStructWithNestedOtherClassUniqueIds(init: SomeStructWithNestedOtherClassUniqueIdsDef.() -> Unit = {}): SomeStructWithNestedOtherClassUniqueIds {
    val def = SomeStructWithNestedOtherClassUniqueIdsDef().apply(init)
    return SomeStructWithNestedOtherClassUniqueIds.create(
        someNestedWithUniqueIds = def.someNestedWithUniqueIds.map { it -> someClassWIthOtherClassUniqueIds(it) },
    )
}

data class NestedClassLevel2Def(
    var uniqueIds: List<(OtherClassWIthUniqueIdDef.() -> Unit)> = emptyList(),
)
fun nestedClassLevel2(init: NestedClassLevel2Def.() -> Unit = {}): NestedClassLevel2 {
    val def = NestedClassLevel2Def().apply(init)
    return NestedClassLevel2.create(
        uniqueIds = def.uniqueIds.map { it -> otherClassWIthUniqueId(it) },
    )
}

data class NestedClassLevel1Def(
    var nestLevel2: List<(NestedClassLevel2Def.() -> Unit)> = emptyList(),
)
fun nestedClassLevel1(init: NestedClassLevel1Def.() -> Unit = {}): NestedClassLevel1 {
    val def = NestedClassLevel1Def().apply(init)
    return NestedClassLevel1.create(
        nestLevel2 = def.nestLevel2.map { it -> nestedClassLevel2(it) },
    )
}

data class ComplexStructureWithNestedUniqueIdsDef(
    var id: String = "someValue",
    var nestLevel1: List<(NestedClassLevel1Def.() -> Unit)> = emptyList(),
)
fun complexStructureWithNestedUniqueIds(init: ComplexStructureWithNestedUniqueIdsDef.() -> Unit = {}): ComplexStructureWithNestedUniqueIds {
    val def = ComplexStructureWithNestedUniqueIdsDef().apply(init)
    return ComplexStructureWithNestedUniqueIds.create(
        id = def.id,
        nestLevel1 = def.nestLevel1.map { it -> nestedClassLevel1(it) },
    )
}

data class NestedValueDef(
    var value: String = "someValue",
)
fun nestedValue(init: NestedValueDef.() -> Unit = {}): NestedValue {
    val def = NestedValueDef().apply(init)
    return NestedValue.create(
        value = def.value,
    )
}

data class OptionalFieldPropertyDef(
    var optionalField: (NestedValueDef.() -> Unit)? = null,
)
fun optionalFieldProperty(init: OptionalFieldPropertyDef.() -> Unit = {}): OptionalFieldProperty {
    val def = OptionalFieldPropertyDef().apply(init)
    return OptionalFieldProperty.create(
        optionalField = def.optionalField?.let { it -> nestedValue(it) },
    )
}

data class CustomTypesPropertyDef(
    var date: String = "01/01/1970 00:00",
    var dateRange: (DateRangeDef.() -> Unit) = {},
)
fun customTypesProperty(init: CustomTypesPropertyDef.() -> Unit = {}): CustomTypesProperty {
    val def = CustomTypesPropertyDef().apply(init)
    return CustomTypesProperty.create(
        date = dateCreate(def.date),
        dateRange = dateRange(def.dateRange),
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
    var id: String = "someValue",
    var other: (OtherDataDef.() -> Unit) = {},
    var custom: Any = Any(),
    var customOpt: Any? = null,
    var goodDataName: String = "someValue",
)
fun someData(init: SomeDataDef.() -> Unit = {}): SomeData {
    val def = SomeDataDef().apply(init)
    return SomeData.create(
        id = SomeId(def.id),
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

data class SomeEventDef(
    var someField: String = "someValue",
    var otherClass: (OtherClassDef.() -> Unit) = {},
)
fun someEvent(init: SomeEventDef.() -> Unit = {}): SomeEvent {
    val def = SomeEventDef().apply(init)
    return SomeEvent.create(
        someField = def.someField,
        otherClass = otherClass(def.otherClass),
    )
}