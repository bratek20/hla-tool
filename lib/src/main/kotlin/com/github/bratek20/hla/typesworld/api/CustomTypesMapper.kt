package com.github.bratek20.hla.typesworld.api

fun worldTypePathCreate(value: String): WorldTypePath {
    return WorldTypePath(value)
}

fun worldTypePathGetValue(it: WorldTypePath): String {
    return it.value
}
