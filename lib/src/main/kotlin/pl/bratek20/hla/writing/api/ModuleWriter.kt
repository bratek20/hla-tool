package pl.bratek20.hla.writing.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path

interface ModuleWriter {
    fun write(path: Path, module: Directory)
}