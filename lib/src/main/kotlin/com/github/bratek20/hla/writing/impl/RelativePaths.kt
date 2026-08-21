package com.github.bratek20.hla.writing.impl

/**
 * Relative ES module specifier from a directory to a file, both given as project
 * relative path parts and the target without its extension.
 *
 * ["main", "SomeModule", "Impl"] -> ["main", "SomeModule", "Api", "ValueObjects"]
 *     gives "../Api/ValueObjects"
 */
fun relativeModuleSpecifier(fromDirectoryParts: List<String>, toParts: List<String>): String {
    val commonPrefixLength = fromDirectoryParts.zip(toParts).takeWhile { (a, b) -> a == b }.count()

    val upwards = List(fromDirectoryParts.size - commonPrefixLength) { ".." }
    val downwards = toParts.drop(commonPrefixLength)

    return if (upwards.isEmpty()) {
        "./" + downwards.joinToString("/")
    } else {
        (upwards + downwards).joinToString("/")
    }
}

fun pathParts(value: String): List<String> {
    return value.split("/").filter { it.isNotEmpty() }
}
