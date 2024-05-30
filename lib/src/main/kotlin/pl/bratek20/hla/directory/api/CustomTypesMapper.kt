package pl.bratek20.hla.directory.api

fun pathCreate(value: String): Path {
    return Path(value)
}

fun pathGetValue(it: Path): String {
    return it.value
}

fun fileContentCreate(value: String): FileContent {
    return FileContent.fromString(value)
}

fun fileContentGetValue(it: FileContent): String {
    return it.toString()
}
