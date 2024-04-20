package pl.bratek20.hla

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl
import pl.bratek20.hla.model.*

fun exampleModule(): HlaModule {
    return HlaModule(
        name = ModuleName("example"),
        simpleValueObjects = listOf(
            SimpleValueObject(
                name = "SomeId",
                typeName = "string"
            ),
            SimpleValueObject(
                name = "SomeId2",
                typeName = "string"
            )
        ),
        complexValueObjects = listOf(
            ComplexValueObject(
                name = "SomeClass",
                fields = listOf(
                    Field(
                        name = "id",
                        type = Type(
                            name = "string",
                        )
                    ),
                    Field(
                        name = "amount",
                        type = Type(
                            name = "int",
                        )
                    )
                )
            ),
            ComplexValueObject(
                name = "SomeClass2",
                fields = listOf(
                    Field(
                        name = "id",
                        type = Type(
                            name = "string",
                        )
                    ),
                    Field(
                        name = "enabled",
                        type = Type(
                            name = "bool",
                        )
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
                        returnType = null,
                        args = listOf(
                            Argument(
                                name = "id",
                                type = Type(
                                    name = "SomeId"
                                )
                            ),
                            Argument(
                                name = "amount",
                                type = Type(
                                    name = "int"
                                )
                            )
                        )
                    ),
                    Method(
                        name = "someQuery",
                        returnType = Type(
                            name = "SomeClass"
                        ),
                        args = listOf(
                            Argument(
                                name = "id",
                                type = Type(
                                    name = "SomeId"
                                )
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

    val dir = ModuleGeneratorImpl().generate(ModuleName("example"), ModuleLanguage.KOTLIN, listOf(module))

    DirectoryLogic().writeDirectory(Path("tmp"), dir)
}