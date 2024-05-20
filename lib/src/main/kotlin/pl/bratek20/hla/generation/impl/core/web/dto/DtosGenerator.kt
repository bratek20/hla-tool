package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.FileGenerator

class DtosGenerator: FileGenerator() {
    override fun name(): String {
        return "Dtos"
    }

    override fun generateFileContent(): FileContent? {
        val dtoTypeFactory = DtoTypeFactory(c.language.types(), c.language.dtoPattern())
        val dtos = (module.complexCustomTypes + module.valueObjects).map {
            dtoTypeFactory.create(apiTypeFactory.create(it))
        }

        return contentBuilder("dtos.vm")
            .put("dtos", dtos)
            .build()
    }
}