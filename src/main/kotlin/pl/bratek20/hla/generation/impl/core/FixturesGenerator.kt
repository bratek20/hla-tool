package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.utils.pascalToCamelCase
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

data class BuilderFieldView(
    val name: String,
    val defType: DefType
) {
    fun constructor(x: String): String {
        return defType.constructor(x)
    }
}

data class BuilderView(
    val funName: String,
    val defName: String,
    val voName: String,
    val fields: List<BuilderFieldView>
)

data class AssertFieldView(
    val name: String,
    val expectedType: ExpectedType
) {
    fun assertion(given: String, expected: String): String {
        return expectedType.assertion(given, expected)
    }

}

data class AssertView(
    val funName: String,
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertFieldView>
)

//TODO support for nested classes
abstract class FixturesGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val types: Types,
    private val domainFactory: DomainFactory = DomainFactory(module, types),
) {
    abstract fun dirName(): String

    abstract fun buildersFileName(): String
    abstract fun buildersContentBuilder(): VelocityFileContentBuilder

    abstract fun assertsFileName(): String
    abstract fun assertsContentBuilder(): VelocityFileContentBuilder
    abstract fun assertFunName(voName: String): String

    fun generateCode(): Directory {
        val buildersFile = buildersFile(module)
        val assertsFile = assertsFile(module)

        return Directory(
            name = dirName(),
            files = listOf(
                buildersFile,
                assertsFile
            )
        )
    }

    private fun defType(type: DomainType): DefType {
        return DefTypeFactory(types).create(type)
    }

    private fun expectedType(type: DomainType): ExpectedType {
        return ExpectedTypeFactory(types).create(type)
    }

    private fun buildersFile(module: HlaModule): File {
        val builders = module.complexValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    val domainType = domainFactory.mapType(it.type)
                    BuilderFieldView(
                        name = it.name,
                        defType = defType(domainType),
                    )
                }
            )
        }

        val fileContent = buildersContentBuilder()
            .put("builders", builders)
            .build()

        return File(
            name = buildersFileName(),
            content = fileContent
        )
    }



    private fun assertsFile(module: HlaModule): File {
        val asserts = module.complexValueObjects.map {
            AssertView(
                funName = assertFunName(it.name),
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    val domainType = domainFactory.mapType(it.type)
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