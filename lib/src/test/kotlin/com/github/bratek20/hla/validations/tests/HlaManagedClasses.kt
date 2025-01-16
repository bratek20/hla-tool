package com.github.bratek20.hla.validations.tests

// example of simple custom type
class Date(
    val value: String,
) {

}

//example of complex custom type
class DateRange {
    init {
        TODO()
    }
}

data class SerializedDateRange(
    private val from: String,
    private val to: String,
) {
    fun toCustomType(): DateRange {
        return dateRangeCreate(
            from = dateCreate(from),
            to = dateCreate(to),
        )
    }

    companion object {
        fun fromCustomType(customType: DateRange): SerializedDateRange {
            return SerializedDateRange(
                from = dateGetValue(dateRangeGetFrom(customType)),
                to = dateGetValue(dateRangeGetTo(customType)),
            )
        }
    }
}

fun dateCreate(value: String): Date {
    return Date(value)
}

fun dateGetValue(it: Date): String {
    return it.value
}

fun dateRangeCreate(from: Date, to: Date): DateRange {
    TODO()
}

fun dateRangeGetFrom(it: DateRange): Date {
    TODO()
}

fun dateRangeGetTo(it: DateRange): Date {
    TODO()
}

