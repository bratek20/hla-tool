package com.some.pkg.somemodule.api

import com.some.pkg.typesmodule.api.*

data class SerializedDateRangeWrapper(
    private val range: SerializedDateRange
) {
    fun getRange(): DateRange {
        return dateRangeCreate(range.getFrom(), range.getTo())
    }

    companion object {
        fun fromCustomType(customType: DateRangeWrapper): SerializedDateRangeWrapper {
            return SerializedDateRangeWrapper(
                range = SerializedDateRange.create(dateRangeWrapperGetRange(customType))
            )
        }

        fun toCustomType(serializedType: SerializedDateRangeWrapper): DateRangeWrapper {
            return dateRangeWrapperCreate(
                range = serializedType.getRange()
            )
        }
    }
}