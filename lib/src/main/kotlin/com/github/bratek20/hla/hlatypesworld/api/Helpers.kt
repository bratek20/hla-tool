package com.github.bratek20.hla.hlatypesworld.api

import com.github.bratek20.hla.typesworld.api.WorldTypePath

fun HlaTypePath.asWorld(): WorldTypePath {
    return WorldTypePath(
        value = value
    )
}

fun WorldTypePath.asHla(): HlaTypePath {
    return HlaTypePath(
        value = value
    )
}

fun WorldTypePath.isHla(): Boolean {
    try {
        asHla().getSubmoduleName()
        return true
    } catch (e: Exception) {
        return false
    }
}



