package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.domain.*
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

data class AssertFieldView(
    val name: String,
    val expectedType: ExpectedViewType
)

data class AssertView(
    val funName: String,
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertFieldView>
)

abstract class AssertsGenerator(
    c: ModulePartGeneratorContext,
    private val languageTypes: LanguageTypes,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(languageTypes),
): ModulePartFileGenerator(c) {

    abstract fun assertsFileName(): String
    abstract fun assertsContentBuilder(): VelocityFileContentBuilder
    abstract fun assertFunName(voName: String): String


    private fun expectedType(type: ViewType): ExpectedViewType {
        return ExpectedTypeFactory(languageTypes).create(type)
    }

    override fun generateFile(): File {
        val asserts = module.complexValueObjects.map {
            AssertView(
                funName = assertFunName(it.name),
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    val domainType = viewTypeFactory.create(it.type, modules)
                    AssertFieldView(
                        name = it.name,
                        expectedType = expectedType(domainType)
                    )
                }
            )
        }

        val fileContent = assertsContentBuilder()
            .put("asserts", asserts)
            .build()

        return File(
            name = assertsFileName(),
            content = fileContent
        )
    }
}