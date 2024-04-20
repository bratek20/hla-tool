package pl.bratek20.hla.generation.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.model.HlaModule

interface ModuleGenerator {
    fun generateModule(moduleName: String, modules: List<HlaModule>, lang: ModuleLanguage): Directory
}