// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.fixtures

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*
import com.some.pkg.typesmodule.api.*
import com.some.pkg.typesmodule.fixtures.*

import com.some.pkg.somemodule.api.*

fun diffSomeId(given: SomeId, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffSomeIntWrapper(given: SomeIntWrapper, expected: Int, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffSomeId2(given: SomeId2, expected: Int, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffSomeEnum(given: SomeEnum, expected: String, path: String = ""): String {
    if (given != SomeEnum.valueOf(expected)) { return "${path}value ${given.name} != ${expected}" }
    return ""
}

data class ExpectedSomeClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun diffSomeClass(given: SomeClass, expectedInit: ExpectedSomeClass.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.getId(), it) != "") { result.add(diffSomeId(given.getId(), it, "${path}id.")) }
    }

    expected.amount?.let {
        if (given.getAmount() != it) { result.add("${path}amount ${given.getAmount()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass2(
    var id: String? = null,
    var names: List<String>? = null,
    var ids: List<String>? = null,
    var enabled: Boolean? = null,
)
fun diffSomeClass2(given: SomeClass2, expectedInit: ExpectedSomeClass2.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass2().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.getId(), it) != "") { result.add(diffSomeId(given.getId(), it, "${path}id.")) }
    }

    expected.names?.let {
        if (given.getNames().size != it.size) { result.add("${path}names size ${given.getNames().size} != ${it.size}"); return@let }
        given.getNames().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}names[${idx}] ${entry} != ${it[idx]}") } }
    }

    expected.ids?.let {
        if (given.getIds().size != it.size) { result.add("${path}ids size ${given.getIds().size} != ${it.size}"); return@let }
        given.getIds().forEachIndexed { idx, entry -> if (diffSomeId(entry, it[idx]) != "") { result.add(diffSomeId(entry, it[idx], "${path}ids[${idx}].")) } }
    }

    expected.enabled?.let {
        if (given.getEnabled() != it) { result.add("${path}enabled ${given.getEnabled()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass3(
    var class2Object: (ExpectedSomeClass2.() -> Unit)? = null,
    var someEnum: String? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
)
fun diffSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass3().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.class2Object?.let {
        if (diffSomeClass2(given.getClass2Object(), it) != "") { result.add(diffSomeClass2(given.getClass2Object(), it, "${path}class2Object.")) }
    }

    expected.someEnum?.let {
        if (diffSomeEnum(given.getSomeEnum(), it) != "") { result.add(diffSomeEnum(given.getSomeEnum(), it, "${path}someEnum.")) }
    }

    expected.class2List?.let {
        if (given.getClass2List().size != it.size) { result.add("${path}class2List size ${given.getClass2List().size} != ${it.size}"); return@let }
        given.getClass2List().forEachIndexed { idx, entry -> if (diffSomeClass2(entry, it[idx]) != "") { result.add(diffSomeClass2(entry, it[idx], "${path}class2List[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass4(
    var otherId: Int? = null,
    var otherClass: (ExpectedOtherClass.() -> Unit)? = null,
    var otherIdList: List<Int>? = null,
    var otherClassList: List<(ExpectedOtherClass.() -> Unit)>? = null,
)
fun diffSomeClass4(given: SomeClass4, expectedInit: ExpectedSomeClass4.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass4().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.otherId?.let {
        if (diffOtherId(given.getOtherId(), it) != "") { result.add(diffOtherId(given.getOtherId(), it, "${path}otherId.")) }
    }

    expected.otherClass?.let {
        if (diffOtherClass(given.getOtherClass(), it) != "") { result.add(diffOtherClass(given.getOtherClass(), it, "${path}otherClass.")) }
    }

    expected.otherIdList?.let {
        if (given.getOtherIdList().size != it.size) { result.add("${path}otherIdList size ${given.getOtherIdList().size} != ${it.size}"); return@let }
        given.getOtherIdList().forEachIndexed { idx, entry -> if (diffOtherId(entry, it[idx]) != "") { result.add(diffOtherId(entry, it[idx], "${path}otherIdList[${idx}].")) } }
    }

    expected.otherClassList?.let {
        if (given.getOtherClassList().size != it.size) { result.add("${path}otherClassList size ${given.getOtherClassList().size} != ${it.size}"); return@let }
        given.getOtherClassList().forEachIndexed { idx, entry -> if (diffOtherClass(entry, it[idx]) != "") { result.add(diffOtherClass(entry, it[idx], "${path}otherClassList[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass5(
    var date: String? = null,
    var dateRange: (ExpectedDateRange.() -> Unit)? = null,
    var dateRangeWrapper: (ExpectedDateRangeWrapper.() -> Unit)? = null,
    var someProperty: (ExpectedSomeProperty.() -> Unit)? = null,
    var otherProperty: (ExpectedOtherProperty.() -> Unit)? = null,
)
fun diffSomeClass5(given: SomeClass5, expectedInit: ExpectedSomeClass5.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass5().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.date?.let {
        if (diffDate(given.getDate(), it) != "") { result.add(diffDate(given.getDate(), it, "${path}date.")) }
    }

    expected.dateRange?.let {
        if (diffDateRange(given.getDateRange(), it) != "") { result.add(diffDateRange(given.getDateRange(), it, "${path}dateRange.")) }
    }

    expected.dateRangeWrapper?.let {
        if (diffDateRangeWrapper(given.getDateRangeWrapper(), it) != "") { result.add(diffDateRangeWrapper(given.getDateRangeWrapper(), it, "${path}dateRangeWrapper.")) }
    }

    expected.someProperty?.let {
        if (diffSomeProperty(given.getSomeProperty(), it) != "") { result.add(diffSomeProperty(given.getSomeProperty(), it, "${path}someProperty.")) }
    }

    expected.otherProperty?.let {
        if (diffOtherProperty(given.getOtherProperty(), it) != "") { result.add(diffOtherProperty(given.getOtherProperty(), it, "${path}otherProperty.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass6(
    var someClassOptEmpty: Boolean? = null,
    var someClassOpt: (ExpectedSomeClass.() -> Unit)? = null,
    var optStringEmpty: Boolean? = null,
    var optString: String? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
    var sameClassList: List<(ExpectedSomeClass6.() -> Unit)>? = null,
)
fun diffSomeClass6(given: SomeClass6, expectedInit: ExpectedSomeClass6.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass6().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.someClassOptEmpty?.let {
        if ((given.getSomeClassOpt() == null) != it) { result.add("${path}someClassOpt empty ${(given.getSomeClassOpt() == null)} != ${it}") }
    }

    expected.someClassOpt?.let {
        if (diffSomeClass(given.getSomeClassOpt()!!, it) != "") { result.add(diffSomeClass(given.getSomeClassOpt()!!, it, "${path}someClassOpt.")) }
    }

    expected.optStringEmpty?.let {
        if ((given.getOptString() == null) != it) { result.add("${path}optString empty ${(given.getOptString() == null)} != ${it}") }
    }

    expected.optString?.let {
        if (given.getOptString()!! != it) { result.add("${path}optString ${given.getOptString()!!} != ${it}") }
    }

    expected.class2List?.let {
        if (given.getClass2List().size != it.size) { result.add("${path}class2List size ${given.getClass2List().size} != ${it.size}"); return@let }
        given.getClass2List().forEachIndexed { idx, entry -> if (diffSomeClass2(entry, it[idx]) != "") { result.add(diffSomeClass2(entry, it[idx], "${path}class2List[${idx}].")) } }
    }

    expected.sameClassList?.let {
        if (given.getSameClassList().size != it.size) { result.add("${path}sameClassList size ${given.getSameClassList().size} != ${it.size}"); return@let }
        given.getSameClassList().forEachIndexed { idx, entry -> if (diffSomeClass6(entry, it[idx]) != "") { result.add(diffSomeClass6(entry, it[idx], "${path}sameClassList[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedClassUsingExternalType(
    var extType: com.some.pkg.legacy.LegacyType? = null,
)
fun diffClassUsingExternalType(given: ClassUsingExternalType, expectedInit: ExpectedClassUsingExternalType.() -> Unit, path: String = ""): String {
    val expected = ExpectedClassUsingExternalType().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.extType?.let {
        if (diffLegacyType(given.getExtType(), it) != "") { result.add(diffLegacyType(given.getExtType(), it, "${path}extType.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedClassHavingOptList(
    var optListEmpty: Boolean? = null,
    var optList: List<(ExpectedSomeClass.() -> Unit)>? = null,
)
fun diffClassHavingOptList(given: ClassHavingOptList, expectedInit: ExpectedClassHavingOptList.() -> Unit, path: String = ""): String {
    val expected = ExpectedClassHavingOptList().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.optListEmpty?.let {
        if ((given.getOptList() == null) != it) { result.add("${path}optList empty ${(given.getOptList() == null)} != ${it}") }
    }

    expected.optList?.let {
        if (given.getOptList()!!.size != it.size) { result.add("${path}optList size ${given.getOptList()!!.size} != ${it.size}"); return@let }
        given.getOptList()!!.forEachIndexed { idx, entry -> if (diffSomeClass(entry, it[idx]) != "") { result.add(diffSomeClass(entry, it[idx], "${path}optList[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedClassHavingOptSimpleVo(
    var optSimpleVoEmpty: Boolean? = null,
    var optSimpleVo: String? = null,
)
fun diffClassHavingOptSimpleVo(given: ClassHavingOptSimpleVo, expectedInit: ExpectedClassHavingOptSimpleVo.() -> Unit, path: String = ""): String {
    val expected = ExpectedClassHavingOptSimpleVo().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.optSimpleVoEmpty?.let {
        if ((given.getOptSimpleVo() == null) != it) { result.add("${path}optSimpleVo empty ${(given.getOptSimpleVo() == null)} != ${it}") }
    }

    expected.optSimpleVo?.let {
        if (diffSomeId(given.getOptSimpleVo()!!, it) != "") { result.add(diffSomeId(given.getOptSimpleVo()!!, it, "${path}optSimpleVo.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedRecordClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun diffRecordClass(given: RecordClass, expectedInit: ExpectedRecordClass.() -> Unit, path: String = ""): String {
    val expected = ExpectedRecordClass().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.id(), it) != "") { result.add(diffSomeId(given.id(), it, "${path}id.")) }
    }

    expected.amount?.let {
        if (given.amount() != it) { result.add("${path}amount ${given.amount()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedClassWithOptExamples(
    var optIntEmpty: Boolean? = null,
    var optInt: Int? = null,
    var optIntWrapperEmpty: Boolean? = null,
    var optIntWrapper: Int? = null,
)
fun diffClassWithOptExamples(given: ClassWithOptExamples, expectedInit: ExpectedClassWithOptExamples.() -> Unit, path: String = ""): String {
    val expected = ExpectedClassWithOptExamples().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.optIntEmpty?.let {
        if ((given.getOptInt() == null) != it) { result.add("${path}optInt empty ${(given.getOptInt() == null)} != ${it}") }
    }

    expected.optInt?.let {
        if (given.getOptInt()!! != it) { result.add("${path}optInt ${given.getOptInt()!!} != ${it}") }
    }

    expected.optIntWrapperEmpty?.let {
        if ((given.getOptIntWrapper() == null) != it) { result.add("${path}optIntWrapper empty ${(given.getOptIntWrapper() == null)} != ${it}") }
    }

    expected.optIntWrapper?.let {
        if (diffSomeIntWrapper(given.getOptIntWrapper()!!, it) != "") { result.add(diffSomeIntWrapper(given.getOptIntWrapper()!!, it, "${path}optIntWrapper.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedClassWithEnumList(
    var enumList: List<String>? = null,
)
fun diffClassWithEnumList(given: ClassWithEnumList, expectedInit: ExpectedClassWithEnumList.() -> Unit, path: String = ""): String {
    val expected = ExpectedClassWithEnumList().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.enumList?.let {
        if (given.getEnumList().size != it.size) { result.add("${path}enumList size ${given.getEnumList().size} != ${it.size}"); return@let }
        given.getEnumList().forEachIndexed { idx, entry -> if (diffSomeEnum(entry, it[idx]) != "") { result.add(diffSomeEnum(entry, it[idx], "${path}enumList[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeQueryInput(
    var id: String? = null,
    var amount: Int? = null,
)
fun diffSomeQueryInput(given: SomeQueryInput, expectedInit: ExpectedSomeQueryInput.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeQueryInput().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.getId(), it) != "") { result.add(diffSomeId(given.getId(), it, "${path}id.")) }
    }

    expected.amount?.let {
        if (given.getAmount() != it) { result.add("${path}amount ${given.getAmount()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeHandlerInput(
    var id: String? = null,
    var amount: Int? = null,
)
fun diffSomeHandlerInput(given: SomeHandlerInput, expectedInit: ExpectedSomeHandlerInput.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeHandlerInput().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.getId(), it) != "") { result.add(diffSomeId(given.getId(), it, "${path}id.")) }
    }

    expected.amount?.let {
        if (given.getAmount() != it) { result.add("${path}amount ${given.getAmount()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeHandlerOutput(
    var id: String? = null,
    var amount: Int? = null,
)
fun diffSomeHandlerOutput(given: SomeHandlerOutput, expectedInit: ExpectedSomeHandlerOutput.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeHandlerOutput().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.id?.let {
        if (diffSomeId(given.getId(), it) != "") { result.add(diffSomeId(given.getId(), it, "${path}id.")) }
    }

    expected.amount?.let {
        if (given.getAmount() != it) { result.add("${path}amount ${given.getAmount()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeProperty(
    var other: (ExpectedOtherProperty.() -> Unit)? = null,
    var id2Empty: Boolean? = null,
    var id2: Int? = null,
    var rangeEmpty: Boolean? = null,
    var range: (ExpectedDateRange.() -> Unit)? = null,
    var doubleExample: Double? = null,
    var longExample: Long? = null,
    var goodName: String? = null,
    var customData: com.github.bratek20.architecture.serialization.api.Struct? = null,
)
fun diffSomeProperty(given: SomeProperty, expectedInit: ExpectedSomeProperty.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeProperty().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.other?.let {
        if (diffOtherProperty(given.getOther(), it) != "") { result.add(diffOtherProperty(given.getOther(), it, "${path}other.")) }
    }

    expected.id2Empty?.let {
        if ((given.getId2() == null) != it) { result.add("${path}id2 empty ${(given.getId2() == null)} != ${it}") }
    }

    expected.id2?.let {
        if (diffSomeId2(given.getId2()!!, it) != "") { result.add(diffSomeId2(given.getId2()!!, it, "${path}id2.")) }
    }

    expected.rangeEmpty?.let {
        if ((given.getRange() == null) != it) { result.add("${path}range empty ${(given.getRange() == null)} != ${it}") }
    }

    expected.range?.let {
        if (diffDateRange(given.getRange()!!, it) != "") { result.add(diffDateRange(given.getRange()!!, it, "${path}range.")) }
    }

    expected.doubleExample?.let {
        if (given.getDoubleExample() != it) { result.add("${path}doubleExample ${given.getDoubleExample()} != ${it}") }
    }

    expected.longExample?.let {
        if (given.getLongExample() != it) { result.add("${path}longExample ${given.getLongExample()} != ${it}") }
    }

    expected.goodName?.let {
        if (given.getGoodName() != it) { result.add("${path}goodName ${given.getGoodName()} != ${it}") }
    }

    expected.customData?.let {
        if (given.getCustomData() != it) { result.add("${path}customData ${given.getCustomData()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeProperty2(
    var value: String? = null,
    var custom: Any? = null,
    var someEnum: String? = null,
    var customOptEmpty: Boolean? = null,
    var customOpt: Any? = null,
)
fun diffSomeProperty2(given: SomeProperty2, expectedInit: ExpectedSomeProperty2.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeProperty2().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.value?.let {
        if (given.getValue() != it) { result.add("${path}value ${given.getValue()} != ${it}") }
    }

    expected.custom?.let {
        if (given.getCustom() != it) { result.add("${path}custom ${given.getCustom()} != ${it}") }
    }

    expected.someEnum?.let {
        if (diffSomeEnum(given.getSomeEnum(), it) != "") { result.add(diffSomeEnum(given.getSomeEnum(), it, "${path}someEnum.")) }
    }

    expected.customOptEmpty?.let {
        if ((given.getCustomOpt() == null) != it) { result.add("${path}customOpt empty ${(given.getCustomOpt() == null)} != ${it}") }
    }

    expected.customOpt?.let {
        if (given.getCustomOpt()!! != it) { result.add("${path}customOpt ${given.getCustomOpt()!!} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedDateRangeWrapper(
    var range: (ExpectedDateRange.() -> Unit)? = null,
)
fun diffDateRangeWrapper(given: DateRangeWrapper, expectedInit: ExpectedDateRangeWrapper.() -> Unit, path: String = ""): String {
    val expected = ExpectedDateRangeWrapper().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.range?.let {
        if (diffDateRange(dateRangeWrapperGetRange(given), it) != "") { result.add(diffDateRange(dateRangeWrapperGetRange(given), it, "${path}range.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeData(
    var other: (ExpectedOtherData.() -> Unit)? = null,
    var custom: Any? = null,
    var customOptEmpty: Boolean? = null,
    var customOpt: Any? = null,
    var goodDataName: String? = null,
)
fun diffSomeData(given: SomeData, expectedInit: ExpectedSomeData.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeData().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.other?.let {
        if (diffOtherData(given.getOther(), it) != "") { result.add(diffOtherData(given.getOther(), it, "${path}other.")) }
    }

    expected.custom?.let {
        if (given.getCustom() != it) { result.add("${path}custom ${given.getCustom()} != ${it}") }
    }

    expected.customOptEmpty?.let {
        if ((given.getCustomOpt() == null) != it) { result.add("${path}customOpt empty ${(given.getCustomOpt() == null)} != ${it}") }
    }

    expected.customOpt?.let {
        if (given.getCustomOpt()!! != it) { result.add("${path}customOpt ${given.getCustomOpt()!!} != ${it}") }
    }

    expected.goodDataName?.let {
        if (given.getGoodDataName() != it) { result.add("${path}goodDataName ${given.getGoodDataName()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeData2(
    var optEnumEmpty: Boolean? = null,
    var optEnum: String? = null,
    var optCustomTypeEmpty: Boolean? = null,
    var optCustomType: String? = null,
)
fun diffSomeData2(given: SomeData2, expectedInit: ExpectedSomeData2.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeData2().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.optEnumEmpty?.let {
        if ((given.getOptEnum() == null) != it) { result.add("${path}optEnum empty ${(given.getOptEnum() == null)} != ${it}") }
    }

    expected.optEnum?.let {
        if (diffSomeEnum(given.getOptEnum()!!, it) != "") { result.add(diffSomeEnum(given.getOptEnum()!!, it, "${path}optEnum.")) }
    }

    expected.optCustomTypeEmpty?.let {
        if ((given.getOptCustomType() == null) != it) { result.add("${path}optCustomType empty ${(given.getOptCustomType() == null)} != ${it}") }
    }

    expected.optCustomType?.let {
        if (diffDate(given.getOptCustomType()!!, it) != "") { result.add(diffDate(given.getOptCustomType()!!, it, "${path}optCustomType.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeEvent(
    var someField: String? = null,
)
fun diffSomeEvent(given: SomeEvent, expectedInit: ExpectedSomeEvent.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeEvent().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.someField?.let {
        if (given.getSomeField() != it) { result.add("${path}someField ${given.getSomeField()} != ${it}") }
    }

    return result.joinToString("\n")
}
fun diffLegacyType(given: com.some.pkg.legacy.LegacyType, expected: com.some.pkg.legacy.LegacyType, path: String = ""): String {
    if (given != expected) { return "${path}value ${given} != ${expected}" }
    return ""
}
