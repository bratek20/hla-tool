package pl.bratek20.hla

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl
import pl.bratek20.hla.model.*

fun exampleModule(): HlaModule {
    return HlaModule(
        name = "example",
        simpleValueObjects = listOf(
            SimpleValueObject(
                name = "SomeId",
                type = "string"
            ),
            SimpleValueObject(
                name = "SomeId2",
                type = "string"
            )
        ),
        complexValueObjects = listOf(
            ComplexValueObject(
                name = "SomeClass",
                fields = listOf(
                    Field(
                        name = "id",
                        type = "string"
                    ),
                    Field(
                        name = "amount",
                        type = "int"
                    )
                )
            ),
            ComplexValueObject(
                name = "SomeClass2",
                fields = listOf(
                    Field(
                        name = "id",
                        type = "string"
                    ),
                    Field(
                        name = "enabled",
                        type = "bool"
                    )
                )
            )
        ),
        interfaces = listOf(
            Interface(
                name = "SomeInterface",
                methods = listOf(
                    Method(
                        name = "someCommand",
                        returnType = "void",
                        args = listOf(
                            Argument(
                                name = "id",
                                type = "SomeId"
                            ),
                            Argument(
                                name = "amount",
                                type = "int"
                            )
                        )
                    ),
                    Method(
                        name = "someQuery",
                        returnType = "SomeClass",
                        args = listOf(
                            Argument(
                                name = "id",
                                type = "SomeId"
                            )
                        )
                    )
                )
            )
        )
    )
}

fun main() {
    val module = exampleModule()

    val dir = ModuleGeneratorImpl().generateCode(module, ModuleLanguage.KOTLIN)

    DirectoryLogic().writeDirectory(Path("tmp"), dir)
}