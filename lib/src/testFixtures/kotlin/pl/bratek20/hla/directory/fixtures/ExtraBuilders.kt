package pl.bratek20.hla.directory.fixtures

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.FileContent

data class FileDefExt(
    var name: String = "SomeName",
    var content: String = "",
    var contentLines: List<String> = emptyList(),
)
fun fileExt(init: FileDefExt.() -> Unit): File {
    val def = FileDefExt().apply(init)
    if (def.contentLines.isNotEmpty()) {
        return File(def.name, FileContent(def.contentLines))
    }
    return File(def.name, FileContent.fromString(def.content))
}