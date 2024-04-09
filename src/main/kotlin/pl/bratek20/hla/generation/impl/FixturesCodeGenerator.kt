package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.Field
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder


data class DefField(
    val name: String,
    val type: String,
    val defaultValue: String
)
data class Def(
    val name: String,
    val fields: List<DefField>
)

data class BuilderVOField(
    val name: String,
    val prefix: String,
    val suffix: String,
)
data class BuilderVO(
    val name: String,
    val fields: List<BuilderVOField>
)

data class BuilderDeclaration(
    val funName: String,
    val def: Def,
    val vo: BuilderVO
)

data class AssertField(
    val name: String,
    val type: String,
    val givenSuffix: String,
)

data class AssertDeclaration(
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertField>
)

class FixturesCodeGenerator(
    private val velocity: VelocityFacade
) {
    fun generateCode(module: HlaModule): Directory {
        val buildersFile = buildersFile(module)
        val assertsFile = assertsFile(module)

        return Directory(
            name = "fixtures",
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
        val declarations = module.complexValueObjects.map {
            BuilderDeclaration(
                funName = pascalToCamelCase(it.name),
                def = toDef(it, module),
                vo = toBuilderVO(it, module)
            )
        }

        val fileContent = contentBuilder("templates/builders.vm", module.name)
            .put("declarations", declarations)
            .build()

        return File(
            name = "Builders.kt",
            content = fileContent
        )
    }

    private fun assertsFile(module: HlaModule): File {
        val declarations = module.complexValueObjects.map {
            AssertDeclaration(
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    AssertField(
                        name = it.name,
                        type = module.findSimpleVO(it.type)?.type ?: it.type,
                        givenSuffix = if (module.findSimpleVO(it.type) != null) ".value" else ""
                    )
                }
            )
        }

        val fileContent = contentBuilder("templates/asserts.vm", module.name)
            .put("declarations", declarations)
            .build()

        return File(
            name = "Asserts.kt",
            content = fileContent
        )
    }

    private fun defaultValue(type: String): String {
        return when (type) {
            "String" -> "\"someValue\""
            "Int" -> "0"
            "Boolean" -> "false"
            else -> "null"
        }
    }

    private fun toDef(vo: ComplexValueObject, module: HlaModule): Def {
        return Def(
            name = vo.name + "Def",
            fields = vo.fields.map {
                val simpleVO = module.findSimpleVO(it.type)
                DefField(
                    name = it.name,
                    type = simpleVO?.type ?: it.type,
                    defaultValue = defaultValue(simpleVO?.type ?: it.type)
                )
            }
        )
    }

    private fun toBuilderVO(vo: ComplexValueObject, module: HlaModule): BuilderVO {
        return BuilderVO(
            name = vo.name,
            fields = vo.fields.map {
                val simpleVO = module.findSimpleVO(it.type)
                var prefix = ""
                var suffix = ""
                if (simpleVO != null) {
                    prefix = simpleVO.name + "("
                    suffix = ")"
                }

                BuilderVOField(
                    name = it.name,
                    prefix = prefix,
                    suffix = suffix
                )
            }
        )
    }

    private fun contentBuilder(templatePath: String, moduleName: String): VelocityFileContentBuilder {
        return contentBuilder(velocity, templatePath, moduleName)
    }
}