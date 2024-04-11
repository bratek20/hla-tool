package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

data class BuilderFieldView(
    val name: String,
    val type: String,
    val defaultValue: String,
    val simpleVOName: String?
) {
    fun isSimpleVO(): Boolean {
        return simpleVOName != null
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
    val type: String,
    val givenSuffix: String,
)

data class AssertView(
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertFieldView>
)

abstract class FixturesGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val types: Types
) {
    abstract fun dirName(): String

    abstract fun buildersFileName(): String
    abstract fun buildersContentBuilder(): VelocityFileContentBuilder

    abstract fun assertsFileName(): String
    abstract fun assertsContentBuilder(): VelocityFileContentBuilder

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

    private fun pascalToCamelCase(name: String): String {
        return name[0].lowercase() + name.substring(1)
    }

    private fun buildersFile(module: HlaModule): File {
        val builders = module.complexValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    val simpleVO = module.findSimpleVO(it.type)
                    BuilderFieldView(
                        name = it.name,
                        type = types.map(simpleVO?.type ?: it.type),
                        defaultValue = types.defaultValue(simpleVO?.type ?: it.type),
                        simpleVOName = simpleVO?.name
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
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    AssertFieldView(
                        name = it.name,
                        type = types.map(module.findSimpleVO(it.type)?.type ?: it.type),
                        givenSuffix = if (module.findSimpleVO(it.type) != null) ".value" else ""
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