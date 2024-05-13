package com.some.pkg.typesmodule.api

data class DateRangeProperty(
    private val from: String,
    private val to: String,
) {
    fun getFrom(): Date {
        return createDate(from)
    }

    fun getTo(): Date {
        return createDate(to)
    }
}