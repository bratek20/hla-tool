// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*
import com.some.pkg.typesmodule.api.*
import com.some.pkg.typesmodule.fixtures.*

import com.some.pkg.somemodule.api.*

fun assertSomeId(given: SomeId, expected: String) {
    val diff = diffSomeId(given, expected)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}


fun assertSomeIntWrapper(given: SomeIntWrapper, expected: Int) {
    val diff = diffSomeIntWrapper(given, expected)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}


fun assertSomeId2(given: SomeId2, expected: Int) {
    val diff = diffSomeId2(given, expected)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass(given: SomeClass, expectedInit: ExpectedSomeClass.() -> Unit) {
    val diff = diffSomeClass(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass2(given: SomeClass2, expectedInit: ExpectedSomeClass2.() -> Unit) {
    val diff = diffSomeClass2(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit) {
    val diff = diffSomeClass3(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass4(given: SomeClass4, expectedInit: ExpectedSomeClass4.() -> Unit) {
    val diff = diffSomeClass4(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass5(given: SomeClass5, expectedInit: ExpectedSomeClass5.() -> Unit) {
    val diff = diffSomeClass5(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeClass6(given: SomeClass6, expectedInit: ExpectedSomeClass6.() -> Unit) {
    val diff = diffSomeClass6(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertClassHavingOptList(given: ClassHavingOptList, expectedInit: ExpectedClassHavingOptList.() -> Unit) {
    val diff = diffClassHavingOptList(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertClassHavingOptSimpleVo(given: ClassHavingOptSimpleVo, expectedInit: ExpectedClassHavingOptSimpleVo.() -> Unit) {
    val diff = diffClassHavingOptSimpleVo(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertRecordClass(given: RecordClass, expectedInit: ExpectedRecordClass.() -> Unit) {
    val diff = diffRecordClass(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertClassWithOptExamples(given: ClassWithOptExamples, expectedInit: ExpectedClassWithOptExamples.() -> Unit) {
    val diff = diffClassWithOptExamples(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertClassWithEnumList(given: ClassWithEnumList, expectedInit: ExpectedClassWithEnumList.() -> Unit) {
    val diff = diffClassWithEnumList(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertClassWithBoolField(given: ClassWithBoolField, expectedInit: ExpectedClassWithBoolField.() -> Unit) {
    val diff = diffClassWithBoolField(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeQueryInput(given: SomeQueryInput, expectedInit: ExpectedSomeQueryInput.() -> Unit) {
    val diff = diffSomeQueryInput(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeHandlerInput(given: SomeHandlerInput, expectedInit: ExpectedSomeHandlerInput.() -> Unit) {
    val diff = diffSomeHandlerInput(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeHandlerOutput(given: SomeHandlerOutput, expectedInit: ExpectedSomeHandlerOutput.() -> Unit) {
    val diff = diffSomeHandlerOutput(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeProperty(given: SomeProperty, expectedInit: ExpectedSomeProperty.() -> Unit) {
    val diff = diffSomeProperty(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeProperty2(given: SomeProperty2, expectedInit: ExpectedSomeProperty2.() -> Unit) {
    val diff = diffSomeProperty2(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomePropertyEntry(given: SomePropertyEntry, expectedInit: ExpectedSomePropertyEntry.() -> Unit) {
    val diff = diffSomePropertyEntry(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeReferencingProperty(given: SomeReferencingProperty, expectedInit: ExpectedSomeReferencingProperty.() -> Unit) {
    val diff = diffSomeReferencingProperty(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertNestedValue(given: NestedValue, expectedInit: ExpectedNestedValue.() -> Unit) {
    val diff = diffNestedValue(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertOptionalFieldProperty(given: OptionalFieldProperty, expectedInit: ExpectedOptionalFieldProperty.() -> Unit) {
    val diff = diffOptionalFieldProperty(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertDateRangeWrapper(given: DateRangeWrapper, expectedInit: ExpectedDateRangeWrapper.() -> Unit) {
    val diff = diffDateRangeWrapper(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeData(given: SomeData, expectedInit: ExpectedSomeData.() -> Unit) {
    val diff = diffSomeData(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeData2(given: SomeData2, expectedInit: ExpectedSomeData2.() -> Unit) {
    val diff = diffSomeData2(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSomeEvent(given: SomeEvent, expectedInit: ExpectedSomeEvent.() -> Unit) {
    val diff = diffSomeEvent(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}