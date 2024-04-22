package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.api.ViewType
import pl.bratek20.hla.generation.impl.core.api.ViewTypeFactory
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.domain.DomainTypeFactory

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

class AssertsGenerator(
    c: ModuleGenerationContext,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(c.modules, c.language.types()),
    private val expectedTypeFactory: ExpectedTypeFactory = ExpectedTypeFactory(c.language.types(), c.language.assertsFixture())
): ModulePartFileGenerator(c) {

    private fun expectedType(type: ViewType): ExpectedViewType {
        return expectedTypeFactory.create(type)
    }

    override fun generateFile(): File {
        val asserts = module.complexValueObjects.map {
            AssertView(
                funName =  language.assertsFixture().assertFunName(it.name),
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    val viewType = viewTypeFactory.create(it.type)
                    AssertFieldView(
                        name = it.name,
                        expectedType = expectedType(viewType)
                    )
                }
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