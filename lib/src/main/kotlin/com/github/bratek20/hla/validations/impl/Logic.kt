package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.properties.api.Property
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.architecture.structs.api.*
import com.github.bratek20.architecture.structs.context.StructsFactory
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.generation.api.PatternName
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
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.logs.api.Logger

import com.github.bratek20.utils.directory.api.*
import java.lang.reflect.ParameterizedType

data class PropertyValuePath(
    val keyName: String,
    val structPath: StructPath
) {
    override fun toString(): String {
        return "\"$keyName\"/$structPath"
    }
}

private class PropertiesTraverser(
    private val properties: List<Property>,
    private val typesWorldApi: TypesWorldApi,
    private val logger: Logger
) {
    fun getPrimitiveValuesAt(path: PropertyValuePath): List<String> {
        return getValuesAt(path).map { it.asPrimitive().value }
    }

    fun getStructValuesAtAsObject(path: PropertyValuePath, type: Class<*>): List<*> {
        val rawStructs = getValuesAt(path).map { it.asObject() }
        val serializer = SerializationFactory.createSerializer()
        return rawStructs.map {
            serializer.fromStruct(it, type)
        }
    }

    fun getPrimitiveValuesAtAsSimpleVO(path: PropertyValuePath, type: Class<*>): List<*> {
        val rawStructs = getValuesAt(path)
            .map { it.asPrimitive() }
            .map {
                struct {
                    "value" to it.value
                }
            }

        val serializer = SerializationFactory.createSerializer()
        return rawStructs.map {
            serializer.fromStruct(it, type)
        }
    }

    private fun getValuesAt(path: PropertyValuePath): List<AnyStruct> {
        val propertyValue = properties.firstOrNull { it.keyName == path.keyName }?.value ?: return emptyList()
        return StructsFactory.createAnyStructHelper().getValues(propertyValue, path.structPath)
    }

    fun findReferences(searchFor: WorldType, propertyKey: KeyDefinition): List<PropertyValuePath> {
        val propertyType = typesWorldApi.getTypeByName(propertyKey.getType().asWorldTypeName())
        val referencePaths = typesWorldApi.getAllReferencesOf(propertyType, searchFor)

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
    private lateinit var traverser: PropertiesTraverser

    override fun validateProperties(hlaFolderPath: Path, profileName: ProfileName, properties: Properties): ValidationResult {
        traverser = PropertiesTraverser(
            properties = properties.getAll(),
            typesWorldApi = typesWorldApi,
            logger = logger
        )

        val group = parser.parse(hlaFolderPath, profileName)

        hlaTypesWorldApi.populate(group)

        val idSourceValidationResult = validateIdSources(group)
        val typeValidatorsResult = executeTypeValidators(group)

        return idSourceValidationResult.merge(typeValidatorsResult)
    }

    private fun validateIdSources(group: ModuleGroup): ValidationResult {
        val sourceInfos = extraInfo.getAllIdSourceInfo()

        logger.info("Source infos: $sourceInfos")
        
        val sourceValidators = sourceInfos.map {
            IdSourceValidator(
                info = it,
                logger = logger,
                group = group,
                traverser = traverser
            )
        }

        return sourceValidators.map { it.validate() }.reduce(ValidationResult::merge)
    }

    private fun executeTypeValidators(group: ModuleGroup): ValidationResult {
        return typeValidators.flatMap { validator ->
            val typeToValidate = getTypeToValidate(validator, 0)

            val typeName = typeToValidate.simpleName
            logger.info("Validating type '$typeName'")
            // Fetch the type information from the world API
            val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))

            // Iterate over all property keys in the group
            group.getAllPropertyKeys().flatMap { propertyKey ->
                // Find references for the current property key
                traverser.findReferences(worldType, propertyKey).map { ref ->
                    if(worldType.getPath().asHla().getPatternName() == PatternName.CustomTypes) {
                        validateCustomTypeForRef(ref, validator)
                    }
                    else {
                        validateTypeForRef(traverser, typeToValidate, ref, validator)
                    }
                }
            }
        }.fold(ValidationResult.ok()) { acc, result -> acc.merge(result) }
    }

    private fun getTypeToValidate(validator: TypeValidator<*>, actualTypeIndex: Int): Class<*> {
        return (validator::class.java.genericInterfaces
            .first { it is ParameterizedType } as ParameterizedType)
            .actualTypeArguments[actualTypeIndex]
            .let { it as Class<*> }
    }

    private fun validateTypeForRef(
        traverser: PropertiesTraverser,
        typeToValidate: Class<*>,
        ref: PropertyValuePath,
        validator: TypeValidator<*>
    ): ValidationResult {
        val objectValues = try {
            traverser.getStructValuesAtAsObject(ref, typeToValidate)
        } catch (e: StructConversionException) {
            traverser.getPrimitiveValuesAtAsSimpleVO(ref, typeToValidate)
        }

        val castedValues = objectValues
            .map {
                typeToValidate.cast(it)
            }

        return validationResult(castedValues, validator, ref)
    }

    private fun validateCustomTypeForRef(
        ref: PropertyValuePath,
        validator: TypeValidator<*>
    ): ValidationResult {
        return validationResult(
            getValuesToValidate(validator, ref),
            validator,
            ref
        )
    }

    private fun getValuesToValidate(validator: TypeValidator<*>, ref: PropertyValuePath): List<Any> {
        val createFunction = getCreateFunction(validator)
        if(validator is SimpleCustomTypeValidator<*, *>) {
            val primitiveValues = traverser.getPrimitiveValuesAt(ref)
            return primitiveValues.map { createFunction(it) }
        }else {
            val typeToValidate = getTypeToValidate(validator, 1)
            val objectValues = traverser.getStructValuesAtAsObject(ref, typeToValidate)
            return objectValues.map { createFunction(it!!)}
        }
    }

    private fun getCreateFunction(validator: TypeValidator<*>): (Any) -> Any {
        return when (validator) {
            is SimpleCustomTypeValidator<*, *> -> validator.createFunction() as (Any) -> Any
            is ComplexCustomTypeValidator<*, *> -> validator.createFunction() as (Any) -> Any
            else -> throw IllegalArgumentException("Unsupported validator type: ${validator::class}")
        }
    }

    private fun validationResult(
        objectValues: List<Any>,
        validator: TypeValidator<*>,
        ref: PropertyValuePath
    ) = objectValues.map { value ->
        (validator as TypeValidator<Any>).validate(value)
    }.map {
        ValidationResult.createFor(it.getErrors().map {
            "Type validator failed at '$ref', message: $it"
        })
    }.fold(ValidationResult.ok()) { acc, result -> acc.merge(result) }
}