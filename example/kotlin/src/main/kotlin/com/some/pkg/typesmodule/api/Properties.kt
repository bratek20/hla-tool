package com.some.pkg.typesmodule.api

data class DataRangeProperty(
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