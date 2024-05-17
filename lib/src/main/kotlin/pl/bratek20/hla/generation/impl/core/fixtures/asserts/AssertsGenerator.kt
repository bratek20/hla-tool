package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.api.ApiType
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

//data class AssertView(
//    val funName: String,
//    val givenName: String,
//    val expectedName: String,
//    val fields: List<AssertFieldView>,
//    val type: ExpectedType,
//    val languageTypes: LanguageTypes
//) {
//    //TODO fix copy paste from dto
//    fun getter(variableName: String, field: AssertFieldView): String {
//        if (type is ComplexCustomExpectedType) {
//            val x = languageTypes.customTypeGetterCall(type.name, field.name)
//            return field.expectedType.assignment("$x($variableName)")
//        }
//        return field.expectedType.assignment("$variableName.${field.name}")
//    }
//}

class AssertsGenerator(
    c: ModuleGenerationContext,
    private val expectedTypeFactory: ExpectedTypeFactory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
): ModulePartFileGenerator(c) {
    override fun generateFile(): File {
        val asserts = (module.complexCustomTypes + module.propertyValueObjects + module.complexValueObjects).map {
            expectedTypeFactory.create(apiTypeFactory.create(it))
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