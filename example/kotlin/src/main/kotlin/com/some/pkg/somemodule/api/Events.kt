package com.some.pkg.somemodule.api

data class SomeEvent(
    private val someField: String,
) {
    fun getSomeField(): String {
        return this.someField
    }

    companion object {
        fun create(
            someField: String,
        ): SomeEvent {
            return SomeEvent(
                someField = someField,
            )
        }
    }
}