package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File

interface FileGenerator {
    fun generateFile(): File
}

interface DirectoryGenerator {
    fun generateDirectory(): Directory
}