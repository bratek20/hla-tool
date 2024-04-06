package pl.bratek20.hla.directory.api

data class Directory(
    val name: String,
    val files: List<File>,
    val directories: List<Directory>
)