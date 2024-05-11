package pl.bratek20.hla.definitions.web

import pl.bratek20.hla.definitions.api.*

data class PropertyMappingDto(
    val key: String,
    val type: TypeDefinitionDto,
) {
    fun toApi(): PropertyMapping {
        return PropertyMapping(
            key = key,
            type = type.toApi(),
        )
    }

    companion object {
        fun fromApi(api: PropertyMapping): PropertyMappingDto {
            return PropertyMappingDto(
                key = api.key,
                type = TypeDefinitionDto.fromApi(api.type),
            )
        }
    }
}

data class EnumDefinitionDto(
    val name: String,
    val values: List<String>,
) {
    fun toApi(): EnumDefinition {
        return EnumDefinition(
            name = name,
            values = values,
        )
    }

    companion object {
        fun fromApi(api: EnumDefinition): EnumDefinitionDto {
            return EnumDefinitionDto(
                name = api.name,
                values = api.values,
            )
        }
    }
}

data class ModuleDefinitionDto(
    val name: String,
    val simpleValueObjects: List<SimpleStructureDefinitionDto>,
    val complexValueObjects: List<ComplexStructureDefinitionDto>,
    val interfaces: List<InterfaceDefinitionDto>,
    val propertyValueObjects: List<ComplexStructureDefinitionDto>,
    val propertyMappings: List<PropertyMappingDto>,
    val enums: List<EnumDefinitionDto>,
) {
    fun toApi(): ModuleDefinition {
        return ModuleDefinition(
            name = ModuleName(name),
            simpleValueObjects = simpleValueObjects.map { it -> it.toApi() },
            complexValueObjects = complexValueObjects.map { it -> it.toApi() },
            interfaces = interfaces.map { it -> it.toApi() },
            propertyValueObjects = propertyValueObjects.map { it -> it.toApi() },
            propertyMappings = propertyMappings.map { it -> it.toApi() },
            enums = enums.map { it -> it.toApi() },
        )
    }

    companion object {
        fun fromApi(api: ModuleDefinition): ModuleDefinitionDto {
            return ModuleDefinitionDto(
                name = api.name.value,
                simpleValueObjects = api.simpleValueObjects.map { it -> SimpleStructureDefinitionDto.fromApi(it) },
                complexValueObjects = api.complexValueObjects.map { it -> ComplexStructureDefinitionDto.fromApi(it) },
                interfaces = api.interfaces.map { it -> InterfaceDefinitionDto.fromApi(it) },
                propertyValueObjects = api.propertyValueObjects.map { it -> ComplexStructureDefinitionDto.fromApi(it) },
                propertyMappings = api.propertyMappings.map { it -> PropertyMappingDto.fromApi(it) },
                enums = api.enums.map { it -> EnumDefinitionDto.fromApi(it) },
            )
        }
    }
}

data class TypeDefinitionDto(
    val name: String,
    val wrappers: List<String>,
) {
    fun toApi(): TypeDefinition {
        return TypeDefinition(
            name = name,
            wrappers = wrappers.map { it -> TypeWrapper.valueOf(it) },
        )
    }

    companion object {
        fun fromApi(api: TypeDefinition): TypeDefinitionDto {
            return TypeDefinitionDto(
                name = api.name,
                wrappers = api.wrappers.map { it -> it.name },
            )
        }
    }
}

data class FieldDefinitionDto(
    val name: String,
    val type: TypeDefinitionDto,
) {
    fun toApi(): FieldDefinition {
        return FieldDefinition(
            name = name,
            type = type.toApi(),
        )
    }

    companion object {
        fun fromApi(api: FieldDefinition): FieldDefinitionDto {
            return FieldDefinitionDto(
                name = api.name,
                type = TypeDefinitionDto.fromApi(api.type),
            )
        }
    }
}

data class SimpleStructureDefinitionDto(
    val name: String,
    val typeName: String,
) {
    fun toApi(): SimpleStructureDefinition {
        return SimpleStructureDefinition(
            name = name,
            typeName = typeName,
        )
    }

    companion object {
        fun fromApi(api: SimpleStructureDefinition): SimpleStructureDefinitionDto {
            return SimpleStructureDefinitionDto(
                name = api.name,
                typeName = api.typeName,
            )
        }
    }
}

data class ComplexStructureDefinitionDto(
    val name: String,
    val fields: List<FieldDefinitionDto>,
) {
    fun toApi(): ComplexStructureDefinition {
        return ComplexStructureDefinition(
            name = name,
            fields = fields.map { it -> it.toApi() },
        )
    }

    companion object {
        fun fromApi(api: ComplexStructureDefinition): ComplexStructureDefinitionDto {
            return ComplexStructureDefinitionDto(
                name = api.name,
                fields = api.fields.map { it -> FieldDefinitionDto.fromApi(it) },
            )
        }
    }
}

data class InterfaceDefinitionDto(
    val name: String,
    val methods: List<MethodDefinitionDto>,
) {
    fun toApi(): InterfaceDefinition {
        return InterfaceDefinition(
            name = name,
            methods = methods.map { it -> it.toApi() },
        )
    }

    companion object {
        fun fromApi(api: InterfaceDefinition): InterfaceDefinitionDto {
            return InterfaceDefinitionDto(
                name = api.name,
                methods = api.methods.map { it -> MethodDefinitionDto.fromApi(it) },
            )
        }
    }
}

data class ArgumentDefinitionDto(
    val name: String,
    val type: TypeDefinitionDto,
) {
    fun toApi(): ArgumentDefinition {
        return ArgumentDefinition(
            name = name,
            type = type.toApi(),
        )
    }

    companion object {
        fun fromApi(api: ArgumentDefinition): ArgumentDefinitionDto {
            return ArgumentDefinitionDto(
                name = api.name,
                type = TypeDefinitionDto.fromApi(api.type),
            )
        }
    }
}

data class ExceptionDefinitionDto(
    val name: String,
) {
    fun toApi(): ExceptionDefinition {
        return ExceptionDefinition(
            name = name,
        )
    }

    companion object {
        fun fromApi(api: ExceptionDefinition): ExceptionDefinitionDto {
            return ExceptionDefinitionDto(
                name = api.name,
            )
        }
    }
}

data class MethodDefinitionDto(
    val name: String,
    val returnType: TypeDefinitionDto,
    val args: List<ArgumentDefinitionDto>,
    val throws: List<ExceptionDefinitionDto>,
) {
    fun toApi(): MethodDefinition {
        return MethodDefinition(
            name = name,
            returnType = returnType.toApi(),
            args = args.map { it -> it.toApi() },
            throws = throws.map { it -> it.toApi() },
        )
    }

    companion object {
        fun fromApi(api: MethodDefinition): MethodDefinitionDto {
            return MethodDefinitionDto(
                name = api.name,
                returnType = TypeDefinitionDto.fromApi(api.returnType),
                args = api.args.map { it -> ArgumentDefinitionDto.fromApi(it) },
                throws = api.throws.map { it -> ExceptionDefinitionDto.fromApi(it) },
            )
        }
    }
}