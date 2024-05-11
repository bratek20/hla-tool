package pl.bratek20.hla.writing.impl

import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.writing.api.ModuleWriter

class ModuleWriterImpl(
    private val directories: Directories
): ModuleWriter {

    override fun write(path: Path, module: Directory) {
        directories.write(path, module)
        directories.write(path, module)
    }
}