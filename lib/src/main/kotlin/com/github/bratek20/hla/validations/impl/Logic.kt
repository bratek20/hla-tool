package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.architecture.structs.api.StructPath
import com.github.bratek20.architecture.structs.context.StructsFactory
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.api.IdSourceInfo
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.queries.api.asWorldTypeName
import com.github.bratek20.hla.queries.api.getAllPropertyKeys
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeKind
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.logs.api.Logger

import com.github.bratek20.utils.directory.api.*

data class PropertyValuePath(
    val keyName: String,
    val structPath: StructPath
) {
    override fun toString(): String {
        return "\"$keyName\"/$structPath"
    }
}

private class WorldTypeTraverser(
    private val typesWorldApi: TypesWorldApi
) {
    fun findAllReferencesOf(target: WorldType, searchFor: WorldType): List<StructPath> {
        if (target == searchFor) {
            return listOf(StructPath(""))
        }

        val kind = typesWorldApi.getTypeInfo(target).getKind()

        if (kind == WorldTypeKind.ClassType) {
            return typesWorldApi.getClassType(target).getFields().flatMap { field ->
                findAllReferencesOf(searchFor, field.getType()).map {
                    StructPath("${field.getName()}/$it")
                }
            }
        }

        if (kind == WorldTypeKind.ConcreteWrapper) {
            return typesWorldApi.getConcreteWrapper(target).getWrappedType().let { wrappedType ->
                findAllReferencesOf(wrappedType, searchFor).map {
                    StructPath("[*]/$it")
                }
            }
        }

        return emptyList()
    }
}

private class PropertiesTraverser(
    private val properties: Properties,
    private val typesWorldApi: TypesWorldApi,
    private val logger: Logger
) {
    fun getPrimitiveValuesAt(path: PropertyValuePath): List<String> {
        val propertyValue = properties.getAll().first { it.keyName == path.keyName }.value
        return StructsFactory.createAnyStructHelper().getValues(propertyValue, path.structPath).map { it.asPrimitive().value }
    }

    fun getStructValuesAt(path: PropertyValuePath, type: Class<*>): List<*> {
        val propertyValue = properties.getAll().first { it.keyName == path.keyName }.value
        val rawStructs = StructsFactory.createAnyStructHelper().getValues(propertyValue, path.structPath).map { it.asObject() }
        val serializer = SerializationFactory.createSerializer()
        return rawStructs.map {
            serializer.fromStruct(it, type)
        }
    }

    fun findReferences(searchFor: WorldType, propertyKey: KeyDefinition): List<PropertyValuePath> {
        val propertyType = typesWorldApi.getTypeByName(propertyKey.getType().asWorldTypeName())
        val referencePaths = WorldTypeTraverser(typesWorldApi).findAllReferencesOf(propertyType, searchFor)

        return referencePaths.map { referencePath ->
            val propertyValuePath = PropertyValuePath(propertyKey.getName(), referencePath)
            logger.info("Found reference for '${searchFor.getName()}' at '$propertyValuePath'")
            propertyValuePath
        }
    }
}

private class IdSourceValidator(
    private val info: IdSourceInfo,
    private val logger: Logger,
    private val group: ModuleGroup,
    private val traverser: PropertiesTraverser
) {
    private val idSourcePath: PropertyValuePath

    init {
        val parentType = info.getParent()
        val parentModule = parentType.getPath().asHla().getModuleName()
        val moduleKeys = BaseModuleGroupQueries(group).get(parentModule).getPropertyKeys()
        val keyName = moduleKeys.first { it.getType().getName() == parentType.getName().value }.getName()

        //I assume that idSource comes always from list and is at the top level
        idSourcePath = PropertyValuePath(keyName, StructPath("[*]/${info.getFieldName()}"))
    }

    fun validate(): ValidationResult {
        val allowedValues = getAllowedValues()

        val allowedValuesValidation = validateAllowedValuesUnique(allowedValues)

        val propertiesValidation = group.getAllPropertyKeys()
            .filter {
                info.getParent().getName().value != it.getType().getName()
            }
            .map { propertyKey ->
                validateProperty(propertyKey, allowedValues)
            }
            .reduce(ValidationResult::merge)

        return allowedValuesValidation.merge(propertiesValidation)
    }

    private fun validateAllowedValuesUnique(allowedValues: List<String>): ValidationResult {
        val countPerValue = allowedValues.groupingBy { it }.eachCount()
        val errors = countPerValue.filter { it.value > 1 }
            .map { "Value '${it.key}' at '${idSourcePath}' is not unique" }
        return ValidationResult.createFor(errors)
    }

    private fun validateProperty(propertyKey: KeyDefinition, allowedValues: List<String>): ValidationResult {
        val errors = mutableListOf<String>()
        val refs = traverser.findReferences(info.getType(), propertyKey)
        refs.forEach {
            val values = traverser.getPrimitiveValuesAt(it)
            logger.info("Values for '$it': $values")

            for (value in values) {
                if (value !in allowedValues) {
                    errors.add("Value '$value' at '$it' not found in source values from '${idSourcePath}'")
                }
            }
        }

        return ValidationResult.createFor(errors)
    }

    private fun getAllowedValues(): List<String> {
        val values = traverser.getPrimitiveValuesAt(idSourcePath)
        logger.info("Allowed values for '${info.getType().getName()}' from source '$idSourcePath': $values")
        return values
    }
}

class HlaValidatorLogic(
    private val parser: ModuleGroupParser,
    private val hlaTypesWorldApi: HlaTypesWorldApi,
    private val extraInfo: HlaTypesExtraInfo,
    private val logger: Logger,
    private val typesWorldApi: TypesWorldApi,
    private val typeValidators: Set<TypeValidator<*>>
): HlaValidator {
    override fun validateProperties(hlaFolderPath: Path, profileName: ProfileName, properties: Properties): ValidationResult {
        val group = parser.parse(hlaFolderPath, profileName)

        hlaTypesWorldApi.populate(group)

        val idSourceValidationResult = validateIdSources(group, properties)
        val typeValidatorsResult = executeTypeValidators(properties, group)

        return idSourceValidationResult.merge(typeValidatorsResult)
    }

    private fun validateIdSources(group: ModuleGroup, properties: Properties): ValidationResult {
        val sourceInfos = extraInfo.getAllIdSourceInfo()

        logger.info("Source infos: $sourceInfos")

        val allPropertiesKeysFromHla = group.getAllPropertyKeys()
        val allKeyNames = allPropertiesKeysFromHla.map { it.getName() }
        logger.info( "Known properties: $allKeyNames")

        val sourceValidators = sourceInfos.map {
            IdSourceValidator(
                info = it,
                logger = logger,
                group = group,
                traverser = PropertiesTraverser(
                    properties = properties,
                    typesWorldApi = typesWorldApi,
                    logger = logger
                )
            )
        }

        return sourceValidators.map { it.validate() }.reduce(ValidationResult::merge)
    }

    private fun createTraverser(properties: Properties): PropertiesTraverser {
        return PropertiesTraverser(
            properties = properties,
            typesWorldApi = typesWorldApi,
            logger = logger
        )
    }

    private fun executeTypeValidators(properties: Properties, group: ModuleGroup): ValidationResult {
        val traverser = createTraverser(properties)
        typeValidators.forEach { validator ->
            val typeToValidate = validator.getType()
            val typeName = typeToValidate.simpleName

            val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))

            group.getAllPropertyKeys()
                .forEach { propertyKey ->
                    traverser.findReferences(worldType, propertyKey).forEach { ref ->
                        traverser.getStructValuesAt(ref, typeToValidate)
                            .map { value ->
                                validator.validate(validator.getType().cast(value))
                            }
                }

        }
        return ValidationResult.ok()
    }
}