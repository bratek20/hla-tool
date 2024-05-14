package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.definitions.api.TypeDefinition
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator
import pl.bratek20.hla.generation.impl.core.api.ApiType
import pl.bratek20.hla.utils.pascalToCamelCase

data class BuilderFieldView(
    val name: String,
    val defType: DefViewType
)

data class BuilderView(
    val funName: String,
    val defName: String,
    val voName: String,
    val fields: List<BuilderFieldView>,
    val constructor: String
)

class BuildersGenerator(
    c: ModuleGenerationContext,
    private val defTypeFactory: DefTypeFactory = DefTypeFactory(c.language.types(), c.language.buildersFixture()),
): ModulePartFileGenerator(c) {

    override fun generateFile(): File {
        val builders = (module.complexValueObjects + module.complexCustomTypes + module.propertyValueObjects).map {
            val isProperty = modules.findPropertyVO(TypeDefinition(it.name, emptyList())) != null
            val isCustom = modules.findComplexCustomType(TypeDefinition(it.name, emptyList())) != null
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    BuilderFieldView(
                        name = it.name,
                        defType = if (isProperty) defType(viewType(it.type).unboxedType())
                            else defType(viewType(it.type)),
                    )
                },
                constructor = if (isProperty) language.types().propertyClassConstructor(it.name)
                    else if (isCustom) language.types().customTypeClassConstructor(it.name)
                    else language.types().classConstructor(it.name)
            )
        }

        val fileContent = contentBuilder("builders.vm")
            .put("builders", builders)
            .build()

        return File(
            name = language.structure().buildersFileName(),
            content = fileContent
        )
    }

    private fun defType(type: ApiType): DefViewType {
        return defTypeFactory.create(type)
    }
}