package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.codebuilder.*
import com.github.bratek20.hla.codebuilder.Function
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.hla.utils.pascalToCamelCase

class DiffsGenerator: FileGenerator() {
    override fun name(): String {
        return "Diffs"
    }

    override fun generateFileContent(): FileContent? {
        val simpleAssertTypes = modules.allSimpleStructureDefinitions(module)
        val enumTypes = modules.allEnumTypeDefinitions(module)
        val complexAssertTypes = modules.allComplexStructureDefinitions(module)
        val externalTypes = modules.allExternalTypesDefinitions(module)
        if (simpleAssertTypes.isEmpty() && complexAssertTypes.isEmpty() && enumTypes.isEmpty() && externalTypes.isEmpty()) {
            return null
        }

        val factory = ExpectedTypeFactory(c)
        val simpleAsserts = (simpleAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        } + (enumTypes).map {
            factory.create(apiTypeFactory.create(it))
        }
        val complexAsserts = (complexAssertTypes).map {
            factory.create(apiTypeFactory.create(it))
        }

        val externalTypesDiffs = if (externalTypes.isEmpty())
            null
        else
            CodeBuilder(lang)
                .add(ManyCodeBlocksSeparatedByLine(
                    externalTypes.map { externalTypeDiff(it) }
                ))
                .build()

        return contentBuilder("diffs.vm")
            .put("simpleAsserts", simpleAsserts)
            .put("complexAsserts", complexAsserts)
            .put("externalTypesDiffs", externalTypesDiffs)
            .build()
    }

    private fun externalTypeDiff(type: TypeDefinition): CodeBlockBuilder {
        val apiType = apiTypeFactory.create(type) as ExternalApiType
        return Function(
            name = "diff" + apiType.rawName,
            args = listOf("given" to apiType.name(), "expected" to apiType.name(), "path" to "String = \"\""),
            returnType = "String",
            body = block {
                line("if (given != expected) { return \"\${path}value \${given} != \${expected}\" }")
                line("return \"\"")
            }
        )
    }
}