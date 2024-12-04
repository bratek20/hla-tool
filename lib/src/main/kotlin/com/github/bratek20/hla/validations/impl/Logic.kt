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
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.logs.api.Logger

import com.github.bratek20.utils.directory.api.*

class HlaValidatorLogic(
    private val parser: ModuleGroupParser,
    private val hlaTypesWorldApi: HlaTypesWorldApi,
    private val extraInfo: HlaTypesExtraInfo,
    private val logger: Logger,
    private val typesWorldApi: TypesWorldApi
): HlaValidator {
    override fun validateProperties(hlaFolderPath: Path, profileName: ProfileName, properties: Properties): ValidationResult {
        val group = parser.parse(hlaFolderPath, profileName)

        hlaTypesWorldApi.populate(group)

        val sourceInfos = extraInfo.getAllIdSourceInfo()

        logger.info("Source infos: $sourceInfos")

        sourceInfos.forEach { sourceInfo ->
            findValuesForIdSource(sourceInfo, group, properties)
        }

        val allPropertiesKeysFromHla = group.getAllPropertyKeys()
        val allKeyNames = allPropertiesKeysFromHla.map { it.getName() }
        logger.info( "Checking properties: $allKeyNames")

        allPropertiesKeysFromHla.forEach { propertyKey ->
            sourceInfos.forEach { sourceInfo ->
                if (sourceInfo.getParent().getName().value != propertyKey.getType().getName()) {
                    val ref = findReference(sourceInfo.getType(), propertyKey)
                    ref?.let {
                        val propValue = getPropertyValue(ref.keyName, properties)
                        val values = StructsFactory.createAnyStructHelper().getValues(propValue, StructPath(ref.structPath)).map { it.value }
                        logger.info("Values for '$ref': $values")
                    }
                }
            }
        }
        //get values of all ids for source
        //get all values for referencing fields, know their path, check if they are in the list
        return ValidationResult(true, emptyList())
    }

    data class PropertyValuePath(
        val keyName: String,
        val structPath: String
    ) {
        override fun toString(): String {
            return "\"$keyName\"/$structPath"
        }
    }

    //Assumes that idSource comes always from list and is at the top level
    private fun findValuesForIdSource(sourceInfo: IdSourceInfo, group: ModuleGroup, properties: Properties) {
        val parentType = sourceInfo.getParent()
        val parentModule = parentType.getPath().asHla().getModuleName()
        val moduleKeys = BaseModuleGroupQueries(group).get(parentModule).getPropertyKeys()
        val keyName = moduleKeys.first { it.getType().getName() == parentType.getName().value }.getName()

        val values = getPropertyValue(keyName, properties).asList().map { it[sourceInfo.getFieldName()] }

        logger.info("Allowed values for '${sourceInfo.getType().getName()}' from source '\"${keyName}\"/[*]/${sourceInfo.getFieldName()}': $values")
    }

    private fun getPropertyValue(keyName: String, properties: Properties): AnyStruct {
        return properties.getAll().first { it.keyName == keyName }.value
    }

    private fun findReference(idSourceType: WorldType, propertyKey: KeyDefinition): PropertyValuePath? {
        val propertyType = typesWorldApi.getTypeByName(propertyKey.getType().asWorldTypeName())
        val referencePath = findReferencePath(idSourceType, propertyType)
        if (referencePath != null) {
            val propertyValuePath = PropertyValuePath(propertyKey.getName(), referencePath.dropLast(1))
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