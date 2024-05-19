package pl.bratek20.hla.directory.api

data class Path(
    val value: String
) {
    fun add(path: Path): Path {
        if (path.value.isEmpty()) {
            return this.copy()
        }
        return Path(value + "/" + path.value)
    }
}