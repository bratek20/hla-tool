package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.properties.api.Property
import com.github.bratek20.architecture.serialization.context.SerializationFactory
import com.github.bratek20.architecture.structs.api.*
import com.github.bratek20.architecture.structs.context.StructsFactory
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.hlatypesworld.api.*
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

data class PropertyValuePathLogic(
    val keyName: String,
    val structPath: StructPath
) {
    override fun toString(): String {
        return "\"$keyName\"/$structPath"
    }

    fun toApi(): PropertyValuePath {
        return PropertyValuePath(toString())
    }
}

private data class ValueWithPath(
    val value: Any,
    val path: PropertyValuePathLogic
)
private class PropertiesTraverser(
    private val properties: List<Property>,
    private val typesWorldApi: TypesWorldApi,
    private val logger: Logger
) {
    companion object {
        private val serializer = SerializationFactory.createSerializer()
    }

    fun getPropertySize(propertyPath: PropertyValuePathLogic): Int {
        return getValuesAt(propertyPath).size
    }

    fun getPrimitiveValuesWithPathAt(path: PropertyValuePathLogic): List<ValueWithPath> {
        return getValuesAt(path).map { ValueWithPath(it.value.asPrimitive().value, PropertyValuePathLogic(path.keyName, it.path)) }
    }

    fun getStructValuesWithPathAtAsObject(path: PropertyValuePathLogic, type: Class<*>): List<ValueWithPath> {
        return getValuesAt(path).map {
            val rawStruct = it.value.asObject()
            val obj = serializer.fromStruct(rawStruct, type)
            ValueWithPath(obj, PropertyValuePathLogic(path.keyName, it.path))
        }
    }

    fun getPrimitiveValuesWithPathAtAsSimpleVO(path: PropertyValuePathLogic, type: Class<*>): List<ValueWithPath> {
        return getValuesAt(path).map {
            val rawValue = it.value.asPrimitive().value
            val simpleVO = serializer.fromStruct(struct {
                "value" to rawValue
            }, type)
            ValueWithPath(simpleVO, PropertyValuePathLogic(path.keyName, it.path))
        }
    }

    private fun getValuesAt(path: PropertyValuePathLogic): List<AnyStructWithPath> {
        val propertyValue = properties.firstOrNull { it.keyName == path.keyName }?.value ?: return emptyList()
        return StructsFactory.createAnyStructHelper().getValues(propertyValue, path.structPath)
    }

    fun findReferences(searchFor: WorldType, propertyKey: KeyDefinition): List<PropertyValuePathLogic> {
        val propertyType = typesWorldApi.getTypeByName(propertyKey.getType().asWorldTypeName())
        val referencePaths = typesWorldApi.getAllReferencesOf(propertyType, searchFor)

        return referencePaths.map { referencePath ->
            val propertyValuePath = PropertyValuePathLogic(propertyKey.getName(), referencePath)
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
    private val idSourcePath: PropertyValuePathLogic

    init {
        val parentType = info.getParent()
        val parentModule = parentType.getPath().asHla().getModuleName()
        val moduleKeys = BaseModuleGroupQueries(group).get(parentModule).getPropertyKeys()
        val keyName = moduleKeys.first { it.getType().getName() == parentType.getName().value }.getName()

        //I assume that idSource comes always from list and is at the top level
        idSourcePath = PropertyValuePathLogic(keyName, StructPath("[*]/${info.getFieldName()}"))
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
            val valuesWithPath = traverser.getPrimitiveValuesWithPathAt(it)
            val values = valuesWithPath.map { it.value }
            logger.info("Values for '$it': $values")
            valuesWithPath.forEach {
                val value = it.value
                if (value !in allowedValues) {
                    errors.add("Value '$value' at '${it.path}' not found in source values from '${idSourcePath}'")
                }
            }
        }

        return ValidationResult.createFor(errors)
    }

    private fun getAllowedValues(): List<String> {
        val values = traverser.getPrimitiveValuesWithPathAt(idSourcePath).map { it.value as String }
        logger.info("Allowed values for '${info.getType().getName()}' from source '$idSourcePath': $values")
        return values
    }
}

private class UniqueIdValidator(
    private val info: UniqueIdInfo,
    private val logger: Logger,
    private val group: ModuleGroup,
    private val traverser: PropertiesTraverser,
    private val typesWorldApi: TypesWorldApi
) {
    private val idPaths: List<PropertyValuePathLogic>

    init {
        val parentType = info.getParent()
        idPaths = findReferences(parentType)
    }

    private fun findReferences(parentType: WorldType): List<PropertyValuePathLogic> {
        val references = mutableListOf<PropertyValuePathLogic>()
        val classTypes = typesWorldApi.getAllClassTypes()
        val parentModule = parentType.getPath().asHla().getModuleName()
        val propertyKeys = BaseModuleGroupQueries(group).get(parentModule).getPropertyKeys()
        classTypes.forEach { classType ->
            val type = classType.getType()
            val propertyKeysFiltered = propertyKeys.filter { it.getType().getName() == type.getName().value }
            propertyKeysFiltered.forEach { propertyKey ->
                if(type.getName() != parentType.getName()) {
                    val referencesForClass = typesWorldApi.getAllReferencesOf(type, parentType)
                    if(referencesForClass.isNotEmpty()) {
                        if(propertyKey.getType().getWrappers().contains(TypeWrapper.LIST)){

                            val propertySize = traverser.getPropertySize(PropertyValuePathLogic(propertyKey.getName(), StructPath("")))
                            for (i in 0 until propertySize) {
                                references.addAll(replaceListWithIndex(referencesForClass, propertyKey, i))
                            }
                        } else {
                            references.addAll(referencesForClass.map { ref -> PropertyValuePathLogic(propertyKey.getName(), StructPath(ref.value + "/${info.getFieldName()}")) })
                        }
                    }
                }
            }
        }
        return references
    }

    private fun replaceListWithIndex(referencesForClass: List<StructPath>, propertyKey: KeyDefinition, i: Int): List<PropertyValuePathLogic> {
        val finalReferences = mutableListOf<PropertyValuePathLogic>()
        for(ref in referencesForClass) {
            val regex = Regex("\\[\\*\\]")
            val count = regex.findAll(ref.value).count()
            if(count > 1) {
                val newRef = ref.value.substringBefore("[*]") + "[*]"
                val sizeNewRef = traverser.getPropertySize(PropertyValuePathLogic(propertyKey.getName(), StructPath("[${i}]/"+newRef)))
                for(j in 0 until sizeNewRef) {
                    val finalRef = newRef.replace("[*]", "[${j}]")
                    val finalPath = PropertyValuePathLogic(propertyKey.getName(), StructPath("[${i}]/"+finalRef+ref.value.substringAfter("[*]")+ "/${info.getFieldName()}"))
                    finalReferences.add(finalPath)
                }
            }else {
                val finalPath = PropertyValuePathLogic(propertyKey.getName(), StructPath("[${i}]/"+ref.value + "/${info.getFieldName()}"))
                finalReferences.add(finalPath)
            }
        }
        return finalReferences
    }

    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()
        idPaths.forEach { idPath ->
            val valuesWithPath = traverser.getPrimitiveValuesWithPathAt(idPath)
            val countPerValue = valuesWithPath.groupingBy { it.value }.eachCount()
            errors.addAll(countPerValue.filter { it.value > 1 }
                .map { "Value '${it.key}' at '${idPath}' is not unique" })
        }
        if(errors.isNotEmpty()) {
            return ValidationResult.createFor(errors)
        }
        return ValidationResult.ok()
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
        val uniqueIdValidationResult = validateUniqueIds(group)

        return idSourceValidationResult.merge(typeValidatorsResult).merge(uniqueIdValidationResult)
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

    private fun validateUniqueIds(group: ModuleGroup): ValidationResult {
        val uniqueIdInfos = extraInfo.getAllUniqueIdInfos()

        logger.info("Unique id infos: $uniqueIdInfos")

        val uniqueIdsValidator = uniqueIdInfos.map {
            UniqueIdValidator(
                info = it,
                logger = logger,
                group = group,
                traverser = traverser,
                typesWorldApi = typesWorldApi
            )
        }

        return uniqueIdsValidator.map { it.validate() }.reduce(ValidationResult::merge)
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
        ref: PropertyValuePathLogic,
        validator: TypeValidator<*>
    ): ValidationResult {
        val objectValues = try {
            traverser.getStructValuesWithPathAtAsObject(ref, typeToValidate)
        } catch (e: StructConversionException) {
            traverser.getPrimitiveValuesWithPathAtAsSimpleVO(ref, typeToValidate)
        }

        val castedValues = objectValues
            .map {
                ValueWithPath(typeToValidate.cast(it.value), it.path)
            }

        return validationResult(castedValues, validator)
    }

    private fun validateCustomTypeForRef(
        ref: PropertyValuePathLogic,
        validator: TypeValidator<*>
    ): ValidationResult {
        return validationResult(
            getValuesToValidate(validator, ref),
            validator
        )
    }

    private fun getValuesToValidate(validator: TypeValidator<*>, ref: PropertyValuePathLogic): List<ValueWithPath> {
        if(validator is SimpleCustomTypeValidator<*, *>) {
            val primitiveValues = traverser.getPrimitiveValuesWithPathAt(ref)
            val createFunction = validator.createFunction() as (Any) -> Any
            return primitiveValues.map { ValueWithPath(createFunction(it.value), it.path) }
        }else {
            val serializedType = getTypeToValidate(validator, 1)
            val objectValues = traverser.getStructValuesWithPathAtAsObject(ref, serializedType)
            return objectValues.map { ValueWithPath(callMethodByName(it.value, "toCustomType"), it.path) }
        }
    }


    fun <T : Any> callMethodByName(target: T, methodName: String, vararg args: Any?): Any {
        val kClass = target::class

        val method = kClass.members.firstOrNull { it.name == methodName }
            ?: throw NoSuchMethodException("Method '$methodName' not found in class '${kClass.qualifiedName}'")

        return method.call(target, *args)!!
    }

    private fun validationResult(
        objectValues: List<ValueWithPath>,
        validator: TypeValidator<*>,
    ): ValidationResult {
      return objectValues.map {
          val value = it.value
          val refWithIndex = it.path
          val result = (validator as TypeValidator<Any>).validate(value, ValidationContext.create(refWithIndex.toApi()))
          ValidationResult.createFor(result.getErrors().map {
              "Type validator failed at '$refWithIndex', message: $it"
          })
      }.fold(ValidationResult.ok()) { acc, result -> acc.merge(result) }
    }
}