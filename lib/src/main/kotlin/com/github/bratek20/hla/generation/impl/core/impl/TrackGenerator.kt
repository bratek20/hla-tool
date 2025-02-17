package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.DependencyConceptDefinition
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.queries.api.asWorldTypeName
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldTypeName



