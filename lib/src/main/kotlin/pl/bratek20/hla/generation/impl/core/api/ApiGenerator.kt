package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.utils.camelToScreamingSnakeCase

class ValueObjectsFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "ValueObjects"
    }

    override fun generateFileContent(): FileContent? {
        if (module.simpleValueObjects.isEmpty() && module.complexValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("valueObjects", ApiValueObjects(
                simpleList = module.simpleValueObjects.map { apiTypeFactory.create(it) },
                complexList = module.complexValueObjects.map { apiTypeFactory.create(it) }
            ))
            .build()
    }

}

class InterfaceFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Interfaces"
    }

    data class ArgumentView(
        val name: String,
        val type: String
    )
    data class MethodView(
        val name: String,
        val returnType: String?,
        val args: List<ArgumentView>,
        val throws: List<String>,
    )
    data class InterfaceView(
        val name: String,
        val methods: List<MethodView>
    )

    override fun generateFileContent(): FileContent?{
        if (module.interfaces.isEmpty()) {
            return null
        }

        return contentBuilder("interfaces.vm")
            .put("interfaces", module.interfaces.map { toView(it) })
            .build()
    }

    private fun toView(interf: InterfaceDefinition): InterfaceView {
        return InterfaceView(
            name = interf.name,
            methods = interf.methods.map { method ->
                MethodView(
                    name = method.name,
                    returnType = toViewType(method.returnType),
                    args = method.args.map { ArgumentView(it.name, toViewType(it.type)) },
                    throws = method.throws.map { it.name }
                )
            }
        )
    }

    private fun toViewType(type: TypeDefinition?): String {
        return apiType(type).name()
    }
}

class PropertiesFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Properties"
    }

    data class ApiPropertyKey(
        val name: String,
        val value: String
    )

    override fun generateFileContent(): FileContent?{
        if (module.propertyValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("properties.vm")
            .put("properties", module.propertyValueObjects.map {
                apiTypeFactory.create<PropertyApiType>(it)
            })
            .put("keys", module.propertyMappings.map { toApiPropertyKey(it) })
            .build()
    }


    private fun toApiPropertyKey(mapping: PropertyMapping): ApiPropertyKey {
        val name = camelToScreamingSnakeCase(mapping.key + "Key")
        return ApiPropertyKey(name, mapping.key)
    }
}

class ExceptionsFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Exceptions"
    }

    override fun generateFileContent(): FileContent?{
        val exceptions = module.interfaces
            .flatMap { it.methods }
            .flatMap { it.throws }
            .map { it.name }
            .distinct()

        if (exceptions.isEmpty()) {
            return null
        }

        return contentBuilder("exceptions.vm")
            .put("exceptions", exceptions)
            .build()
    }
}

class EnumsFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "Enums"
    }

    override fun generateFileContent(): FileContent?{
        if (module.enums.isEmpty()) {
            return null
        }

        return contentBuilder("enums.vm")
            .put("enums", module.enums)
            .build()
    }
}

class CustomTypesFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "CustomTypes"
    }

    override fun generateFileContent(): FileContent?{
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        val classNames = module.simpleCustomTypes.map { it.name } +
            module.complexCustomTypes.map { it.name }

        return contentBuilder("customTypes.vm")
            .put("classNames", classNames)
            .build()
    }
}

class CustomTypesMapperFileGenerator: FileGenerator() {
    override fun getBaseFileName(): String {
        return "CustomTypesMapper"
    }

    override fun generateFileContent(): FileContent?{
        if (module.simpleCustomTypes.isEmpty() && module.complexCustomTypes.isEmpty()) {
            return null
        }

        return contentBuilder("customTypesMapper.vm")
            .put("customTypes", ApiCustomTypes(
                simpleList = module.simpleCustomTypes.map { apiTypeFactory.create(it) },
                complexList = module.complexCustomTypes.map { apiTypeFactory.create(it) }
            ))
            .build()
    }
}

class ApiGenerator: DirectoryGenerator() {
    override fun getDirectoryName(): String {
        return "api"
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            ValueObjectsFileGenerator(),
            InterfaceFileGenerator(),
            PropertiesFileGenerator(),
            ExceptionsFileGenerator(),
            EnumsFileGenerator(),
            CustomTypesFileGenerator(),
            CustomTypesMapperFileGenerator()
        )
    }
}