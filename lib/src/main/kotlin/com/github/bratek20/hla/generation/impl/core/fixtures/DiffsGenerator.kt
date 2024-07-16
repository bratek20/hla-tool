package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.*
import com.github.bratek20.codebuilder.builders.FunctionBuilder
import com.github.bratek20.codebuilder.builders.FunctionBuilderOps
import com.github.bratek20.codebuilder.builders.function
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.utils.directory.api.FileContent

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
                .addMany(
                    externalTypes.map { externalTypeDiff(it) }
                )
                .build()

        return contentBuilder("diffs.vm")
            .put("simpleAsserts", simpleAsserts)
            .put("complexAsserts", complexAsserts)
            .put("externalTypesDiffs", externalTypesDiffs)
            .build()
    }

    private fun externalTypeDiff(type: TypeDefinition): FunctionBuilder {
        val apiType = apiTypeFactory.create(type) as ExternalApiType
        return function {
            name = "diff" + apiType.rawName
            addArg {
                name = "given"
                this.type = type(apiType.name())
            }
            addArg {
                name = "expected"
                this.type = type(apiType.name())
            }
            addArg {
                name = "path"
                this.type = baseType(BaseType.STRING)
                defaultValue = "\"\""
            }
            returnType = baseType(BaseType.STRING)
            body = {
                line("if (given != expected) { return \"\${path}value \${given} != \${expected}\" }")
                line("return \"\"")
            }
        }
    }
}