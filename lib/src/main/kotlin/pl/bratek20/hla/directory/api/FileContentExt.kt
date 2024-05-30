package pl.bratek20.hla.directory.api

fun fileContentFromString(content: String): FileContent {
    return FileContent(content.split("\\n|\\r\\n".toRegex()))
}
