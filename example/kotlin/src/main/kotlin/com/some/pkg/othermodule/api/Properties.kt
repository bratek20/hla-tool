package com.some.pkg.othermodule.api

data class OtherProperty(
    private val id: String,
    val name: String,
) {
    fun getId(): OtherId {
        return OtherId(this.id)
    }
}

val OTHER_PROPERTY_KEY = pl.bratek20.architecture.properties.api.ObjectPropertyKey(
    "otherProperty",
    OtherProperty::class
)