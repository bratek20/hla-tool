package pl.bratek20.othermodule.api

import pl.bratek20.architecture.properties.api.PropertyKey

val OTHER_PROPERTY_KEY = PropertyKey("otherProperty")

data class OtherProperty(
    private val id: String,
    val name: String,
) {
    fun getId(): OtherId {
        return OtherId(this.id)
    }
}