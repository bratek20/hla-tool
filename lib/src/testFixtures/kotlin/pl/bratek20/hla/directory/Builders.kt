package pl.bratek20.hla.directory

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent

data class FileDef(
    var name: String = "SomeName",
    var content: String = "",
    var contentLines: List<String> = emptyList(),
)
fun file(init: FileDef.() -> Unit): File {
    val def = FileDef().apply(init)
    if (def.contentLines.isNotEmpty()) {
        return File(def.name, FileContent(def.contentLines))
    }
    return File(def.name, FileContent.fromString(def.content))
}

data class DirectoryDef(
    var name: String = "SomeName",
    var files: List<FileDef.() -> Unit> = emptyList(),
    var directories: List<DirectoryDef.() -> Unit> = emptyList(),
)
fun directory(init: DirectoryDef.() -> Unit): Directory {
    val def = DirectoryDef().apply(init)
    return Directory(
        name = def.name,
        files = def.files.map { file(it) },
        directories = def.directories.map { directory(it) },
    )
}