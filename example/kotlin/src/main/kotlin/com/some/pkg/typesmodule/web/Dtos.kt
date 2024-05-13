package com.some.pkg.typesmodule.web

import com.some.pkg.typesmodule.api.*

data class DateRangeDto(
    val from: String,
    val to: String,
) {
    fun toApi(): DateRange {
        return createDateRange(
            from = createDate(from),
            to = createDate(to),
        )
    }

    companion object {
        fun fromApi(api: DateRange): DateRangeDto {
            return DateRangeDto(
                from = getDateValue(getDateRangeFrom(api)),
                to = getDateValue(getDateRangeTo(api)),
            )
        }
    }
}