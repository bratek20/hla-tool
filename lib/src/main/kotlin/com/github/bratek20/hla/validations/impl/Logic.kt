package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.properties.api.Property
import com.github.bratek20.architecture.structs.api.AnyStruct
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

private class PropertiesTraverser(
    private val properties: Properties
) {
    fun getPropertyValuesAt(path: PropertyValuePath): List<String> {
        val propertyValue = properties.getAll().first { it.keyName == path.keyName }.value
        return StructsFactory.createAnyStructHelper().getValues(propertyValue, path.structPath).map { it.asPrimitive().value }
    }
}

private class IdSourceValidator(
    private val info: IdSourceInfo,
    private val logger: Logger,
    private val typesWorldApi: TypesWorldApi,
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
        val ref = findReference(info.getType(), propertyKey)
        ref?.let {
            val values = traverser.getPropertyValuesAt(it)
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
        val values = traverser.getPropertyValuesAt(idSourcePath)
        logger.info("Allowed values for '${info.getType().getName()}' from source '$idSourcePath': $values")
        return values
    }

    private fun findReference(idSourceType: WorldType, propertyKey: KeyDefinition): PropertyValuePath? {
        val propertyType = typesWorldApi.getTypeByName(propertyKey.getType().asWorldTypeName())
        val referencePath = findReferencePath(idSourceType, propertyType)
        if (referencePath != null) {
            val propertyValuePath = PropertyValuePath(propertyKey.getName(), StructPath(referencePath.dropLast(1)))
            logger.info("Found reference for '${idSourceType.getName()}' at '$propertyValuePath'")
            return propertyValuePath
        }
        return null
    }

    private fun findReferencePath(idSourceType: WorldType, currentType: WorldType): String? {
        if (currentType == idSourceType) {
            return ""
        }
        val kind = typesWorldApi.getTypeInfo(currentType).getKind()
        if (kind == WorldTypeKind.ClassType) {
            typesWorldApi.getClassType(currentType).getFields().firstOrNull {
                findReferencePath(idSourceType, it.getType()) != null
            }?.let {
                return "${it.getName()}/${findReferencePath(idSourceType, it.getType())}"
            }
        }
        if (kind == WorldTypeKind.ConcreteWrapper) {
            typesWorldApi.getConcreteWrapper(currentType).getWrappedType().let {
                findReferencePath(idSourceType, it)?.let {
                    return "[*]/$it"
                }
            }
        }
        return null
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
        val typeValidatorsResult = executeTypeValidators(properties)

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
                typesWorldApi = typesWorldApi,
                group = group,
                traverser = PropertiesTraverser(properties)
            )
        }

        return sourceValidators.map { it.validate() }.reduce(ValidationResult::merge)
    }

    private fun executeTypeValidators(properties: Properties): ValidationResult {
        typeValidators.forEach {
            val typeToValidate = it.getType()
            val typeName = typeToValidate.simpleName

            val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))
        }
        return ValidationResult.ok()
    }
}