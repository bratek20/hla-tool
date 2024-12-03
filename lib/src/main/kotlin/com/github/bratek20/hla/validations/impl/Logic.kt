package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpSupport
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.impl.HlaTypesWorldApiLogic
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.validations.api.*

import com.github.bratek20.utils.directory.api.*

class HlaValidatorLogic(
    private val parser: ModuleGroupParser,
    private val hlaTypesWorldApi: HlaTypesWorldApi,
    private val extraInfo: HlaTypesExtraInfo
): HlaValidator {
    override fun validateProperties(hlaFolderPath: Path, profileName: ProfileName, properties: Properties): ValidationResult {
        val group = parser.parse(hlaFolderPath, profileName)
        populate(group)
        extraInfo.getAllIdSourceInfo()

        //get values of all ids for source
        //get all values for referencing fields, know their path, check if they are in the list
        return ValidationResult(true, emptyList())
    }

    private fun populate(group: ModuleGroup) {
        //val queries = BaseModuleGroupQueries(group)
        //val apiTypeFactory = ApiTypeFactoryLogic(queries, group.getProfile().getLanguage())

        //(hlaTypesWorldApi as HlaTypesWorldApiLogic).apiTypeFactory = context.apiTypeFactory
        hlaTypesWorldApi.populate(group)
    }
}