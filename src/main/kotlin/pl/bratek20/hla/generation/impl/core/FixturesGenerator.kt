package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.utils.pascalToCamelCase
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

data class BuilderFieldView(
    val name: String,
    val type: String,
    val defType: String,
    val defaultValue: String,
    val isList: Boolean,
    val simpleVOName: String?,
    val defType2: DefType
) {
    fun isSimpleVO(): Boolean {
        return simpleVOName != null
    }

    fun constructor(x: String): String {
        return defType2.constructor(x)
    }
}

data class BuilderView(
    val funName: String,
    val defName: String,
    val voName: String,
    val fields: List<BuilderFieldView>
)

data class AssertFieldView(
    val name: String,
    val type: String,
    val isList: Boolean,
    val isSimpleVO: Boolean,
    val isComplexVO: Boolean,
    val defType: String,
    val defType2: ExpectedType
) {
    fun assertion(given: String, expected: String): String {
        return defType2.assertion(given, expected)
    }

}

data class AssertView(
    val funName: String,
    val givenName: String,
    val expectedName: String,
    val fields: List<AssertFieldView>
)

//TODO support for nested classes
abstract class FixturesGenerator(
    protected val module: HlaModule,
    protected val velocity: VelocityFacade,
    private val types: Types,
    private val oldDomainFactory: OldDomainFactory = OldDomainFactory(module),
    private val domainFactory: DomainFactory = DomainFactory(module, types),
) {
    abstract fun dirName(): String

    abstract fun buildersFileName(): String
    abstract fun buildersContentBuilder(): VelocityFileContentBuilder

    abstract fun assertsFileName(): String
    abstract fun assertsContentBuilder(): VelocityFileContentBuilder
    abstract fun assertFunName(voName: String): String

    fun generateCode(): Directory {
        val buildersFile = buildersFile(module)
        val assertsFile = assertsFile(module)

        return Directory(
            name = dirName(),
            files = listOf(
                buildersFile,
                assertsFile
            )
        )
    }

    private fun defType(type: DomainType): DefType {
        return DefTypeFactory(types).create(type)
    }

    private fun expectedType(type: DomainType): ExpectedType {
        return ExpectedTypeFactory(types).create(type)
    }

    private fun buildersFile(module: HlaModule): File {
        val builders = module.complexValueObjects.map {
            BuilderView(
                funName = pascalToCamelCase(it.name),
                defName = it.name + "Def",
                voName = it.name,
                fields = it.fields.map {
                    val oldDomainType = oldDomainFactory.mapType(it.type)
                    val domainType = domainFactory.mapType(it.type)
                    BuilderFieldView(
                        name = it.name,
                        type = types.map(oldDomainType.unbox()),
                        defType = defType(domainType).toView(),
                        defaultValue = defType(domainType).defaultValue(),
                        isList = oldDomainType.isList,
                        simpleVOName = if(oldDomainType.isBoxed) oldDomainType.name else null,
                        defType2 = defType(domainType)
                    )
                }
            )
        }

        val fileContent = buildersContentBuilder()
            .put("builders", builders)
            .build()

        return File(
            name = buildersFileName(),
            content = fileContent
        )
    }



    private fun assertsFile(module: HlaModule): File {
        val asserts = module.complexValueObjects.map {
            AssertView(
                funName = assertFunName(it.name),
                givenName = it.name,
                expectedName = "Expected${it.name}",
                fields = it.fields.map {
                    val oldDomainType = oldDomainFactory.mapType(it.type)
                    val domainType = domainFactory.mapType(it.type)
                    AssertFieldView(
                        name = it.name,
                        type = types.map(oldDomainType.unbox()),
                        isList = oldDomainType.isList,
                        isSimpleVO = oldDomainType.isBoxed,
                        isComplexVO = oldDomainType.kind == TypeKind.COMPLEX_VO,
                        defType = expectedType(domainType).toView(),
                        defType2 = expectedType(domainType)
                    )
                }
            )
        }

        val fileContent = assertsContentBuilder()
            .put("asserts", asserts)
            .build()

        return File(
            name = assertsFileName(),
            content = fileContent
        )
    }

}