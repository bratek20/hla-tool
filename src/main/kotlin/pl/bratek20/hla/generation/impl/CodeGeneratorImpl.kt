package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.api.CodeGenerator
import pl.bratek20.hla.model.ComplexValueObject
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.model.Interface
import pl.bratek20.hla.model.SimpleValueObject
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder
import pl.bratek20.hla.velocity.impl.VelocityFacadeImpl

class CodeGeneratorImpl : CodeGenerator {
    private val velocity = VelocityFacadeImpl() // TODO proper injection

    private val apiGenerator: ApiCodeGenerator = ApiCodeGenerator(velocity)
    private val fixturesGenerator: FixturesCodeGenerator = FixturesCodeGenerator(velocity)

    override fun generateCode(module: HlaModule): Directory {
        val apiCode = apiGenerator.generateCode(module)
        val fixturesCode = fixturesGenerator.generateCode(module)

        return Directory(
            name = module.name.lowercase(),
            directories = listOf(
                apiCode,
                fixturesCode
            )
        )
    }
}