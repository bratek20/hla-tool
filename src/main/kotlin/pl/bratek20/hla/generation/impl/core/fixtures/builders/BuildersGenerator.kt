package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.*
import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.generation.impl.core.domain.ViewType
import pl.bratek20.hla.generation.impl.core.domain.ViewTypeFactory
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.utils.pascalToCamelCase
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

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

abstract class BuildersGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val languageTypes: LanguageTypes,
    private val viewTypeFactory: ViewTypeFactory = ViewTypeFactory(languageTypes),
): FileGenerator {
    abstract fun buildersFileName(): String
    abstract fun buildersContentBuilder(): VelocityFileContentBuilder

    override fun generateFile(): File {
        val builders = module.complexValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    val domainType = viewTypeFactory.create(it.type, module)
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

    private fun defType(type: ViewType): DefViewType {
        return DefTypeFactory(languageTypes).create(type)
    }
}