package pl.bratek20.hla.directory.fixtures

import pl.bratek20.hla.directory.api.Directory

data class DirectoryDef(
    var name: String = "SomeName",
    var files: List<FileDefExt.() -> Unit> = emptyList(),
    var directories: List<DirectoryDef.() -> Unit> = emptyList(),
)
fun directory(init: DirectoryDef.() -> Unit): Directory {
    val def = DirectoryDef().apply(init)
    return Directory(
        name = def.name,
        files = def.files.map { fileExt(it) },
        directories = def.directories.map { directory(it) },
    )
}