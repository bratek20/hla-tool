package com.github.bratek20.hla.typesworld.api

import com.github.bratek20.hla.typesworld.api.HlaTypePath

fun hlaTypePathCreate(value: String): HlaTypePath {
    return HlaTypePath(value)
}

fun hlaTypePathGetValue(it: HlaTypePath): String {
    return it.value
}
