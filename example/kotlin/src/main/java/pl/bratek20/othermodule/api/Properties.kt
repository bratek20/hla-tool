package pl.bratek20.othermodule.api

data class OtherProperty(
    private val id: String,
    val name: String,
) {
    fun getId(): OtherId {
        return OtherId(this.id)
    }
}