// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.othermodule.api

data class OtherData(
    private val id: Int,
) {
    fun getId(): OtherId {
        return OtherId(this.id)
    }
}

val OTHER_DATA_KEY = pl.bratek20.architecture.properties.api.ObjectPropertyKey(
    "otherData",
    OtherData::class
)