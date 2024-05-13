package com.some.pkg.typesmodule.fixtures

import com.some.pkg.typesmodule.api.*

data class DateRangeDef(
    var from: String = "someValue",
    var to: String = "someValue"
)
fun dateRange(init: DateRangeDef.() -> Unit = {}): DateRange {
    val def = DateRangeDef().apply(init)
    return createDateRange(
        createDate(def.from),
        createDate(def.to)
    )
}

data class DateRangePropertyDef(
    var from: String = "someValue",
    var to: String = "someValue"
)
fun dateRangeProperty(init: DateRangePropertyDef.() -> Unit = {}): DateRangeProperty {
    val def = DateRangePropertyDef().apply(init)
    return DateRangeProperty(
        from = def.from,
        to = def.to
    )
}