package com.some.pkg.typesmodule.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.some.pkg.typesmodule.api.*

data class ExpectedDateRange(
    var from: String? = null,
    var to: String? = null,
)
fun assertDateRange(given: DateRange, expectedInit: ExpectedDateRange.() -> Unit) {
    val expected = ExpectedDateRange().apply(expectedInit)

    expected.from?.let {
        assertThat(getDateValue(getDateRangeFrom(given))).isEqualTo(it)
    }

    expected.to?.let {
        assertThat(getDateValue(getDateRangeTo(given))).isEqualTo(it)
    }
}