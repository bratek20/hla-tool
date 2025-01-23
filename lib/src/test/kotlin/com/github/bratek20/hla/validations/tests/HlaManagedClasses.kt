package com.github.bratek20.hla.validations.tests

// example of simple custom type
class Date(
    val value2: String, // to test that value field is not assumed for simple custom types
) {

}

//example of complex custom type
class DateRange (
    val from: Date,
    val to: Date
){
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
    return it.value2
}

fun dateRangeCreate(from: Date, to: Date): DateRange {
    return DateRange(
        from = from,
        to = to
    )
}

fun dateRangeGetFrom(it: DateRange): Date {
    return it.from
}

fun dateRangeGetTo(it: DateRange): Date {
    return it.to
}

