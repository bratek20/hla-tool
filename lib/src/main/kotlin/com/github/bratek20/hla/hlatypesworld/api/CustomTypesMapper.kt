package com.github.bratek20.hla.hlatypesworld.api

fun hlaTypePathCreate(value: String): HlaTypePath {
    return HlaTypePath(value)
}

fun hlaTypePathGetValue(it: HlaTypePath): String {
    return it.value
}