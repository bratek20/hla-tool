package pl.bratek20.hla.directory.api

fun pathCreate(value: String): Path {
    return Path(value)
}

fun pathGetValue(it: Path): String {
    return it.value
}
