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

data class FileContent(
    val lines: List<String>
) {
    override fun toString(): String {
        return lines.joinToString("\n")
    }

    companion object {
        fun fromString(content: String): FileContent {
            return FileContent(content.split("\\n|\\r\\n".toRegex()))
        }
    }
}