// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.othermodule.api

data class OtherData(
    private var id: Int,
) {
    fun getId(): OtherId {
        return OtherId(this.id)
    }

    fun setId(id: OtherId) {
        this.id = id.value
    }

    companion object {
        fun create(
            id: OtherId,
        ): OtherData {
            return OtherData(
                id = id.value,
            )
        }
    }

    fun update(other: OtherData) {
        this.id = other.id
    }
}