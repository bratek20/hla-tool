package pl.bratek20.hla.generation.impl.core.web.dto

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.api.ApiType
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

data class DtoFieldView(
    val name: String,
    val type: DtoType<*>
)

data class DtoView(
    val apiName: String,
    val apiConstructor: String,
    val dtoName: String,
    val fields: List<DtoFieldView>,
    val type: DtoType<*>,
    val languageTypes: LanguageTypes
) {
    fun getter(variableName: String, field: DtoFieldView): String {
        if (type is ComplexCustomDtoType) {
            val x = languageTypes.customTypeGetterName(apiName, field.name)
            return field.type.assignment("$x($variableName)")
        }
        return field.type.assignment("$variableName.${field.name}")
    }
}

class DtosGenerator(
    c: ModuleGenerationContext,
    private val dtoViewTypeFactory: DtoViewTypeFactory = DtoViewTypeFactory(c.language.types(), c.language.dtoPattern())
): ModulePartFileGenerator(c) {

    private fun myViewType(def: ComplexStructureDefinition): ApiType {
        val type = TypeDefinition(def.name, emptyList())
        return viewType(type)
    }

    private fun myDtoViewType(def: ComplexStructureDefinition): DtoType<*> {
        return dtoViewTypeFactory.create(myViewType(def))
    }

    private fun constructorName(def: ComplexStructureDefinition): String {
        val viewType = myViewType(def)
        return viewType.constructorName()
    }

    override fun generateFile(): File {
        val dtos = (module.complexValueObjects + module.complexCustomTypes).map {
            DtoView(
                apiName = it.name,
                apiConstructor = constructorName(it),
                dtoName = it.name + "Dto",
                fields = it.fields.map {
                    val viewType = dtoViewTypeFactory.create(viewType(it.type))
                    DtoFieldView(
                        name = it.name,
                        type = viewType,
                    )
                },
                type = myDtoViewType(it),
                languageTypes = language.types()
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