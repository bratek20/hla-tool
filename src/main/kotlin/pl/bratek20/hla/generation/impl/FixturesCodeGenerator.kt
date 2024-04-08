package pl.bratek20.hla.generation.impl

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

class FixturesCodeGenerator(
    private val velocity: VelocityFacade
) {
    fun generateCode(module: HlaModule): Directory {
        val buildersFile = buildersFile(module)
        return Directory(
            name = "fixtures",
            files = listOf(
                buildersFile
            )
        )
    }

    private fun buildersFile(module: HlaModule): File {
        val vo = module.complexValueObjects[0]
        val def = toDef(vo, module)
        val builderVO = toBuilderVO(vo, module)

        val fileContent = contentBuilder("templates/builders.vm", module.name)
            .put("def", def)
            .put("funName", "someClass")
            .put("vo", builderVO)
            .build()

        return File(
            name = "Builders.kt",
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