package pl.bratek20.hla.directory.api

class Path(
    initValue: String
) {
    val value = normalized(initValue)

    fun add(path: Path): Path {
        if (path.value.isEmpty()) {
            return Path(value)
        }
        return Path(value + "\\" + path.value)
    }

    private fun normalized(value: String): String {
        return value.replace("\\", "/")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Path) return false

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value
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