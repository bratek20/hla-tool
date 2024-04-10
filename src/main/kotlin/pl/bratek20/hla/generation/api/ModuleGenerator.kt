package pl.bratek20.hla.generation.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.model.HlaModule

interface ModuleGenerator {
    fun generateCode(module: HlaModule, lang: ModuleLanguage): Directory
}