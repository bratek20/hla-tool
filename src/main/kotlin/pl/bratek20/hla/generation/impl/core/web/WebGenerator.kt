package pl.bratek20.hla.generation.impl.core.web

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.web.dto.DtosGenerator

class WebGenerator(
    private val c: ModuleGenerationContext,
) {
    fun generateDirectory(): Directory {
        val dtoClassesFile = DtosGenerator(c).generateFile()

        return Directory(
            name = c.language.structure().webDirName(),
            files = listOf(
                dtoClassesFile
            )
        )
    }
}