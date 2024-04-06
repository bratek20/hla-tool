package pl.bratek20.hla.directory.api

data class FileContent(
    val lines: List<String>
) {
    companion object {
        fun fromString(content: String): FileContent {
            return FileContent(content.split("\n"))
        }
    }
}
