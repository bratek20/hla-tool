package pl.bratek20.hla.directory.api

data class Path(
    val value: String
) {
    fun add(path: Path): Path {
        return Path(value + "/" + path.value)
    }
}
