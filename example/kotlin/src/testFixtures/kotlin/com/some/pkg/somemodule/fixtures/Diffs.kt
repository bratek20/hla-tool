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


fun diffSomeId2(given: SomeId2, expected: Int, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

data class ExpectedSomeProperty(
    var other: (ExpectedOtherProperty.() -> Unit)? = null,
    var id2: Int? = null,
    var range: (ExpectedDateRange.() -> Unit)? = null,
)
fun diffSomeProperty(given: SomeProperty, expectedInit: ExpectedSomeProperty.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeProperty().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.other?.let {
        if (diffOtherProperty(given.getOther(), it) != "") { result.add(diffOtherProperty(given.getOther(), it, "${path}other.")) }
    }

    expected.id2?.let {
        if (diffSomeId2(given.getId2()!!, it) != "") { result.add(diffSomeId2(given.getId2()!!, it, "${path}id2.")) }
    }

    expected.range?.let {
        if (diffDateRange(given.getRange()!!, it) != "") { result.add(diffDateRange(given.getRange()!!, it, "${path}range.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeProperty2(
    var value: String? = null,
    var custom: Any? = null,
    var someEnum: SomeEnum? = null,
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
        if (given.getSomeEnum() != it) { result.add("${path}someEnum ${given.getSomeEnum()} != ${it}") }
    }

    expected.customOpt?.let {
        if (given.getCustomOpt()!! != it) { result.add("${path}customOpt ${given.getCustomOpt()!!} != ${it}") }
    }

    return result.joinToString("\n")
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
        if (given.getNames().size != it.size) { result.add("${path}names size ${given.getNames().size} != ${it.size}") }
        given.getNames().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}names[${idx}] ${entry} != ${it[idx]}") } }
    }

    expected.ids?.let {
        if (given.getIds().size != it.size) { result.add("${path}ids size ${given.getIds().size} != ${it.size}") }
        given.getIds().forEachIndexed { idx, entry -> if (diffSomeId(entry, it[idx]) != "") { result.add(diffSomeId(entry, it[idx], "${path}ids[${idx}].")) } }
    }

    expected.enabled?.let {
        if (given.getEnabled() != it) { result.add("${path}enabled ${given.getEnabled()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSomeClass3(
    var class2Object: (ExpectedSomeClass2.() -> Unit)? = null,
    var someEnum: SomeEnum? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
)
fun diffSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass3().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.class2Object?.let {
        if (diffSomeClass2(given.getClass2Object(), it) != "") { result.add(diffSomeClass2(given.getClass2Object(), it, "${path}class2Object.")) }
    }

    expected.someEnum?.let {
        if (given.getSomeEnum() != it) { result.add("${path}someEnum ${given.getSomeEnum()} != ${it}") }
    }

    expected.class2List?.let {
        if (given.getClass2List().size != it.size) { result.add("${path}class2List size ${given.getClass2List().size} != ${it.size}") }
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
        if (given.getOtherIdList().size != it.size) { result.add("${path}otherIdList size ${given.getOtherIdList().size} != ${it.size}") }
        given.getOtherIdList().forEachIndexed { idx, entry -> if (diffOtherId(entry, it[idx]) != "") { result.add(diffOtherId(entry, it[idx], "${path}otherIdList[${idx}].")) } }
    }

    expected.otherClassList?.let {
        if (given.getOtherClassList().size != it.size) { result.add("${path}otherClassList size ${given.getOtherClassList().size} != ${it.size}") }
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
    var someClassOpt: (ExpectedSomeClass.() -> Unit)? = null,
    var optString: String? = null,
    var sameClassList: List<(ExpectedSomeClass6.() -> Unit)>? = null,
)
fun diffSomeClass6(given: SomeClass6, expectedInit: ExpectedSomeClass6.() -> Unit, path: String = ""): String {
    val expected = ExpectedSomeClass6().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.someClassOpt?.let {
        if (diffSomeClass(given.getSomeClassOpt()!!, it) != "") { result.add(diffSomeClass(given.getSomeClassOpt()!!, it, "${path}someClassOpt.")) }
    }

    expected.optString?.let {
        if (given.getOptString()!! != it) { result.add("${path}optString ${given.getOptString()!!} != ${it}") }
    }

    expected.sameClassList?.let {
        if (given.getSameClassList().size != it.size) { result.add("${path}sameClassList size ${given.getSameClassList().size} != ${it.size}") }
        given.getSameClassList().forEachIndexed { idx, entry -> if (diffSomeClass6(entry, it[idx]) != "") { result.add(diffSomeClass6(entry, it[idx], "${path}sameClassList[${idx}].")) } }
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
    var customOpt: Any? = null,
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

    expected.customOpt?.let {
        if (given.getCustomOpt()!! != it) { result.add("${path}customOpt ${given.getCustomOpt()!!} != ${it}") }
    }

    return result.joinToString("\n")
}