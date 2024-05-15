package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.api.ApiType
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

data class AssertFieldView(
    val name: String,
    val expectedType: ExpectedType
)

data class AssertView(
    val funName: String,
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertFieldView>,
    val type: ExpectedType,
    val languageTypes: LanguageTypes
) {
    //TODO fix copy paste from dto
    fun getter(variableName: String, field: AssertFieldView): String {
        if (type is ComplexCustomExpectedType) {
            val x = languageTypes.customTypeGetterName(type.name, field.name)
            return field.expectedType.assignment("$x($variableName)")
        }
        return field.expectedType.assignment("$variableName.${field.name}")
    }
}

class AssertsGenerator(
    c: ModuleGenerationContext,
    private val expectedTypeFactory: ExpectedTypeFactory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
): ModulePartFileGenerator(c) {

    //TODO fix copy paste from dto
    private fun myViewType(def: ComplexStructureDefinition): ApiType {
        val type = TypeDefinition(def.name, emptyList())
        return apiType(type)
    }

    private fun myExpectedViewType(def: ComplexStructureDefinition): ExpectedType {
        return expectedTypeFactory.create(myViewType(def))
    }

    private fun expectedType(type: ApiType): ExpectedType {
        return expectedTypeFactory.create(type)
    }

    override fun generateFile(): File {
        val asserts = (module.complexValueObjects + module.complexCustomTypes).map {
            AssertView(
                funName =  language.assertsFixture().assertFunName(it.name),
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    AssertFieldView(
                        name = it.name,
                        expectedType = expectedType(apiType(it.type))
                    )
                },
                type = myExpectedViewType(it),
                languageTypes = language.types()
            )
        }

        val fileContent = contentBuilder("asserts.vm")
            .put("asserts", asserts)
            .build()

        return File(
            name = language.structure().assertsFileName(),
            content = fileContent
        )
    }
}