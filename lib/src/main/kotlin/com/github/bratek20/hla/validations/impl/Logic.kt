package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.api.IdSourceInfo
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.queries.api.getAllPropertyKeys
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.logs.api.Logger

import com.github.bratek20.utils.directory.api.*

class HlaValidatorLogic(
    private val parser: ModuleGroupParser,
    private val hlaTypesWorldApi: HlaTypesWorldApi,
    private val extraInfo: HlaTypesExtraInfo,
    private val logger: Logger
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
        //get values of all ids for source
        //get all values for referencing fields, know their path, check if they are in the list
        return ValidationResult(true, emptyList())
    }

    //Assumes that idSource comes always from list and is at the top level
    private fun findValuesForIdSource(sourceInfo: IdSourceInfo, group: ModuleGroup, properties: Properties) {
        val parentType = sourceInfo.getParent()
        val parentModule = parentType.getPath().asHla().getModuleName()
        val moduleKeys = BaseModuleGroupQueries(group).get(parentModule).getPropertyKeys()
        val keyName = moduleKeys.first { it.getType().getName() == parentType.getName().value }.getName()

        val prop = properties.getAll().first { it.keyName == keyName }
        val values = prop.value.asList().map { it[sourceInfo.getFieldName()] }

        logger.info("Allowed values for '${sourceInfo.getType().getName()}' from source '\"${keyName}\"/[*]/${sourceInfo.getFieldName()}': $values")
    }
}