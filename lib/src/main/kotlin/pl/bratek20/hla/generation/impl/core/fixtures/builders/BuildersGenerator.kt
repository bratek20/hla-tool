package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.api.ViewType
import pl.bratek20.hla.generation.impl.core.api.ViewTypeFactory
import pl.bratek20.hla.utils.pascalToCamelCase

data class BuilderFieldView(
    val name: String,
    val defType: DefViewType
)

data class BuilderView(
    val funName: String,
    val defName: String,
    val voName: String,
    val fields: List<BuilderFieldView>
)

class BuildersGenerator(
    c: ModuleGenerationContext,
    private val defTypeFactory: DefTypeFactory = DefTypeFactory(c.language.types(), c.language.buildersFixture()),
): ModulePartFileGenerator(c) {

    override fun generateFile(): File {
        val complexVoBuilders = module.complexValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    BuilderFieldView(
                        name = it.name,
                        defType = defType(viewType(it.type)),
                    )
                }
            )
        }

        val propertyVoBuilders = module.propertyValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    BuilderFieldView(
                        name = it.name,
                        defType = defType(viewType(it.type).unboxedType()),
                    )
                }
            )
        }

        val fileContent = contentBuilder("builders.vm")
            .put("builders", complexVoBuilders + propertyVoBuilders)
            .put("complexVoBuilders", complexVoBuilders)
            .put("propertyVoBuilders", propertyVoBuilders)
            .build()

        return File(
            name = language.structure().buildersFileName(),
            content = fileContent
        )
    }

    private fun defType(type: ViewType): DefViewType {
        return defTypeFactory.create(type)
    }
}