package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.domain.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.domain.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.domain.*

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
    c: ModuleGenerationContext,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(c.language.types()),
    private val expectedTypeFactory: ExpectedTypeFactory = ExpectedTypeFactory(c.language.types(), c.language.moreTypes())
): ModulePartFileGenerator(c) {

    abstract fun assertsFileName(): String
    abstract fun assertFunName(voName: String): String

    private fun expectedType(type: ViewType): ExpectedViewType {
        return expectedTypeFactory.create(type)
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

        val fileContent = contentBuilder("asserts.vm")
            .put("asserts", asserts)
            .build()

        return File(
            name = assertsFileName(),
            content = fileContent
        )
    }
}