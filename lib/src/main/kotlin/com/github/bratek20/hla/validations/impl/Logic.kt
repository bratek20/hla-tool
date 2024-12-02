package com.github.bratek20.hla.validations.impl

import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.hla.validations.api.*

import com.github.bratek20.utils.directory.api.*

class HlaValidatorLogic: HlaValidator {
    override fun validateProperties(hlaFolderPath: Path, properties: Properties): ValidationResult {
        //read modules
        //find all id sources
        //find all fields of type having id source
        //get values of all ids for source
        //get all values for referencing fields, know their path, check if they are in the list
        return ValidationResult(true, emptyList())
    }
}