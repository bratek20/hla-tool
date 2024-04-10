package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.ComplexValueObject
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

abstract class FixturesGenerator(
    private val module: HlaModule,
    private val velocity: VelocityFacade
) {
    abstract fun dirName(): String
    abstract fun buildersFileName(): String
    abstract fun assertsFileName(): String
    abstract fun templatesPathPrefix(): String

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
        val declarations = module.complexValueObjects.map {
            BuilderDeclaration(
                funName = pascalToCamelCase(it.name),
                def = toDef(it, module),
                vo = toBuilderVO(it, module)
            )
        }

        val fileContent = contentBuilder("builders.vm")
            .put("declarations", declarations)
            .build()

        return File(
            name = buildersFileName(),
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

        val fileContent = contentBuilder("asserts.vm")
            .put("declarations", declarations)
            .build()

        return File(
            name = assertsFileName(),
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

    private fun contentBuilder(templateName: String): VelocityFileContentBuilder {
        return contentBuilder(velocity, templatesPathPrefix() + "/" + templateName, module.name)
    }
}