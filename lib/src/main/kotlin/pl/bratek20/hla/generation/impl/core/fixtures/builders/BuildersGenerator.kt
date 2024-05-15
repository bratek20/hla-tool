package pl.bratek20.hla.generation.impl.core.fixtures.builders

import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.ModulePartFileGenerator

//val isProperty = modules.findPropertyVO(TypeDefinition(it.name, emptyList())) != null
//val isCustom = modules.findComplexCustomType(TypeDefinition(it.name, emptyList())) != null
//BuilderView(
//funName = pascalToCamelCase(it.name),
//defName = it.name + "Def",
//voName = it.name,
//fields = it.fields.map {
//    BuilderFieldView(
//        name = it.name,
//        defType = if (isProperty) defType(apiType(it.type).unboxedType())
//        else defType(apiType(it.type)),
//    )
//},
//constructor = if (isProperty) language.types().propertyClassConstructor(it.name)
//else if (isCustom) language.types().customTypeConstructorCall(it.name)
//else language.types().classConstructor(it.name)
//)

class BuildersGenerator(
    c: ModuleGenerationContext,
    private val defTypeFactory: DefTypeFactory = DefTypeFactory(c.language.buildersFixture()),
): ModulePartFileGenerator(c) {

    override fun generateFile(): File {
        val builders = (module.complexValueObjects + module.complexCustomTypes + module.propertyValueObjects).map {
            defTypeFactory.create(apiTypeFactory.create(it))
        }

        val fileContent = contentBuilder("builders.vm")
            .put("builders", builders)
            .build()

        return File(
            name = language.structure().buildersFileName(),
            content = fileContent
        )
    }
}