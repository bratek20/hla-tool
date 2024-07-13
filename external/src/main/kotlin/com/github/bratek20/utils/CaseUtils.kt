package com.github.bratek20.utils

fun pascalToCamelCase(name: String): String {
    return name[0].lowercase() + name.substring(1)
}

fun camelToPascalCase(name: String): String {
    return name[0].uppercase() + name.substring(1)
}

fun camelToScreamingSnakeCase(name: String): String {
    return name.replace(Regex("([a-z])([A-Z])")) {
        "${it.groupValues[1]}_${it.groupValues[2]}"
    }.uppercase()
}