package pl.bratek20.hla.directory.api

import java.util.function.Consumer

class FileContentBuilder {
    private val lines: MutableList<String> = ArrayList()
    private var spaces = 0

    fun addLine(line: String): FileContentBuilder {
        lines.add(currentSpaces() + line)

        return this
    }

    fun addContent(content: FileContent): FileContentBuilder {
        content.lines.forEach(Consumer { line: String -> this.addLine(line) })
        return this
    }

    fun addIndent(spaces: Int): FileContentBuilder {
        this.spaces += spaces
        return this
    }

    fun removeIndent(spaces: Int): FileContentBuilder {
        this.spaces -= spaces
        return this
    }

    fun build(): FileContent {
        return FileContent(lines)
    }

    private fun currentSpaces(): String {
        return " ".repeat(spaces)
    }
}
