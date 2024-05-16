package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.api.ApiType
import pl.bratek20.hla.generation.impl.core.api.ComplexStructureApiType

class DtosGenerator(
    c: ModuleGenerationContext,
    private val dtoViewTypeFactory: DtoViewTypeFactory = DtoViewTypeFactory(c.language.types(), c.language.dtoPattern())
): ModulePartFileGenerator(c) {

    private fun createDtoType(def: ComplexStructureDefinition): ComplexStructureDtoType<*> {
        val apiType = apiTypeFactory.create<ComplexStructureApiType<*>>(def)
        return dtoViewTypeFactory.create(apiType) as ComplexStructureDtoType
    }

    override fun generateFile(): File {
        val dtos = (module.complexCustomTypes + module.complexValueObjects).map {
            createDtoType(it)
        }

        val fileContent = contentBuilder("dtos.vm")
            .put("dtos", dtos)
            .build()

        return File(
            name = language.structure().dtosFileName(),
            content = fileContent
        )
    }
}