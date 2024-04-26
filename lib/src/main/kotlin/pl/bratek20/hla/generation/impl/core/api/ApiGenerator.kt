package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.ModulePartDirectoryGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.definitions.ComplexStructureDefinition
import pl.bratek20.hla.definitions.InterfaceDefinition
import pl.bratek20.hla.definitions.SimpleStructureDefinition
import pl.bratek20.hla.definitions.TypeDefinition
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

class ApiGenerator(
    c: ModuleGenerationContext
): ModulePartDirectoryGenerator(c) {

    override fun generateDirectory(): Directory {
        val files = mutableListOf<File>()
        valueObjectsFile()?.let { files.add(it) }
        interfacesFile()?.let { files.add(it) }
        propertiesFile()?.let { files.add(it) }

        return Directory(
            name = language.structure().apiDirName(),
            files = files
        )
    }

    private fun valueObjectsFile(): File? {
        if (module.simpleValueObjects.isEmpty() && module.complexValueObjects.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("valueObjects.vm")
            .put("valueObjects", ValueObjectsView(
                simpleList = module.simpleValueObjects.map { toView(it) },
                complexList = module.complexValueObjects.map { toView(it) }
            ))
            .build()

        return File(
            name = language.structure().valueObjectsFileName(),
            content = fileContent
        )
    }

    private fun interfacesFile(): File? {
        if (module.interfaces.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("interfaces.vm")
            .put("interfaces", module.interfaces.map { toView(it) })
            .build()

        return File(
            name = language.structure().interfacesFileName(),
            content = fileContent
        )
    }

    private fun propertiesFile(): File? {
        if (module.propertyValueObjects.isEmpty()) {
            return null
        }

        val fileContent = contentBuilder("properties.vm")
            .put("valueObjects", module.propertyValueObjects.map { toPropertyView(it) })
            .build()

        return File(
            name = language.structure().propertiesFileName(),
            content = fileContent
        )
    }


    private fun toView(vo: SimpleStructureDefinition): SimpleValueObjectView {
        return SimpleValueObjectView(
            name = vo.name,
            type = toViewType(vo.type())
        )
    }
    private fun toView(vo: ComplexStructureDefinition): ComplexValueObjectView {
        return ComplexValueObjectView(
            name = vo.name,
            fields = vo.fields.map { FieldView(it.name, viewType(it.type)) }
        )
    }

    data class GetterView(
        val name: String,
        val type: ViewType,
        val field: String
    )
    data class PropertyFieldView(
        val name: String,
        val accessor: String,
        val type: ViewType
    )
    data class PropertyValueObjectView(
        val name: String,
        val fields: List<PropertyFieldView>,
        val getters: List<GetterView>
    )
    private fun toPropertyView(vo: ComplexStructureDefinition): PropertyValueObjectView {
        return PropertyValueObjectView(
            name = vo.name,
            fields = vo.fields.map {
                val typeView = viewType(it.type)
                val accessor = if (typeView is SimpleVOViewType) {
                    "private "
                } else {
                    ""
                }
                PropertyFieldView(it.name, accessor, typeView)
           },
            getters = vo.fields
                .filter { viewType(it.type) is SimpleVOViewType }
                .map { GetterView(getterName(it.name), viewType(it.type), it.name) }
        )
    }

    private fun getterName(fieldName: String): String {
        return "get${camelToPascalCase(fieldName)}"
    }

    private fun toView(interf: InterfaceDefinition): InterfaceView {
        return InterfaceView(
            name = interf.name,
            methods = interf.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = toViewType(method.returnType),
                    args = method.args.map { ArgumentView(it.name, toViewType(it.type)) }
                )
            }
        )
    }

    private fun toViewType(type: TypeDefinition?): String {
        return viewType(type).name()
    }
}