package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator

data class DtoFieldView(
    val name: String,
    val type: DtoViewType
)

data class DtoView(
    val dtoName: String,
    val apiName: String,
    val fields: List<DtoFieldView>
)

class DtosGenerator(
    c: ModuleGenerationContext,
    private val dtoViewTypeFactory: DtoViewTypeFactory = DtoViewTypeFactory(c.language.types()),
): ModulePartFileGenerator(c) {

    override fun generateFile(): File {
        val dtos = module.complexValueObjects.map {
            DtoView(
                apiName = it.name,
                dtoName = it.name + "Dto",
                fields = it.fields.map {
                    val viewType = dtoViewTypeFactory.create(viewType(it.type))
                    DtoFieldView(
                        name = it.name,
                        type = viewType,
                    )
                }
            )
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