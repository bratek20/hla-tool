// DO NOT EDIT! Autogenerated by HLA tool

package com.other.pkg.importingmodule.api

import com.some.pkg.othermodule.api.*

data class ImportingProperty(
    private val other: OtherProperty,
) {
    fun getOther(): OtherProperty {
        return this.other
    }

    companion object {
        fun create(
            other: OtherProperty,
        ): ImportingProperty {
            return ImportingProperty(
                other = other,
            )
        }
    }
}