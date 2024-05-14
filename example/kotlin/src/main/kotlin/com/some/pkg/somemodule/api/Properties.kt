package com.some.pkg.somemodule.api

import com.some.pkg.othermodule.api.OtherProperty

data class SomeProperty(
    val other: OtherProperty,
) {
}