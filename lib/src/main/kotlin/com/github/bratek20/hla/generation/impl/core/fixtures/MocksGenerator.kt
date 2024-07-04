package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.codebuilder.*
import com.github.bratek20.hla.codebuilder.Function
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    class View(
        val interfaceName: String,
        val moduleName: String
    ) {
        fun classes(): String {
            return CodeBuilder()
                .line(ClassDeclaration("${interfaceName}Mock", interfaceName))
                .tab()
                .line("// referenceOtherClass")
                .line(ListFieldDeclaration("referenceOtherClassCalls", "OtherClass"))
                .line(ListFieldDeclaration("referenceOtherClassResponses", "Pair<ExpectedOtherClass.() -> Unit, OtherClassDef.() -> Unit>"))
                .emptyLine()
                .add(Function(
                    name = "setReferenceOtherClassResponse",
                    args = listOf(
                        Pair("args", "ExpectedOtherClass.() -> Unit"),
                        Pair("response", "OtherClassDef.() -> Unit")
                    ),
                    body = OneLineBlock("referenceOtherClassResponses.add(Pair(args, response))")
                ))
                .emptyLine()
                .add(Function(
                    override = true,
                    name = "referenceOtherClass",
                    returnType = "OtherClass",
                    args = listOf(Pair("other", "OtherClass")),
                    body = block {
                        line("referenceOtherClassCalls.add(other)")
                        line("return otherClass(referenceOtherClassResponses.find { diffOtherClass(other, it.first) == \"\" }?.second ?: {})")
                    }
                ))
                .emptyLine()
                .add(Function(
                    name = "assertReferenceOtherClassCalled",
                    args = listOf(Pair("times", "Int = 1")),
                    body = block {
                        line("assertThat(referenceOtherClassCalls.size).withFailMessage(\"Expected referenceOtherClass to be called \$times times, but was called \$referenceOtherClassCalls times\").isEqualTo(times)")
                    }
                ))
                .emptyLine()
                .add(Function(
                    name = "assertReferenceOtherClassCalledForArgs",
                    args = listOf(
                        Pair("args", "ExpectedOtherClass.() -> Unit"),
                        Pair("times", "Int = 1")
                    ),
                    body = block {
                        line("val calls = referenceOtherClassCalls.filter { diffOtherClass(it, args) == \"\" }")
                        line("assertThat(calls.size).withFailMessage(\"Expected referenceOtherClass to be called \$times times, but was called \$referenceOtherClassCalls times\").isEqualTo(times)")
                    }
                ))
                .emptyLine()
                .line("// referenceLegacyType")
                .line(ListFieldDeclaration("referenceLegacyTypeCalls", "com.some.pkg.legacy.LegacyType"))
                .line(ListFieldDeclaration("referenceLegacyTypeResponses", "Pair<com.some.pkg.legacy.LegacyType, com.some.pkg.legacy.LegacyType>"))
                .emptyLine()
                .add(Function(
                    name = "setReferenceLegacyTypeResponse",
                    args = listOf(
                        Pair("args", "com.some.pkg.legacy.LegacyType"),
                        Pair("response", "com.some.pkg.legacy.LegacyType")
                    ),
                    body = OneLineBlock("referenceLegacyTypeResponses.add(Pair(args, response))")
                ))
                .emptyLine()
                .add(Function(
                    override = true,
                    name = "referenceLegacyType",
                    returnType = "com.some.pkg.legacy.LegacyType",
                    args = listOf(Pair("legacyType", "com.some.pkg.legacy.LegacyType")),
                    body = block {
                        line("referenceLegacyTypeCalls.add(legacyType)")
                        line("return referenceLegacyTypeResponses.find { it.first == legacyType }?.second ?: legacyType")
                    }
                ))
                .emptyLine()
                .add(Function(
                    name = "assertReferenceLegacyTypeCalled",
                    args = listOf(Pair("times", "Int = 1")),
                    body = block {
                        line("assertThat(referenceLegacyTypeCalls.size).withFailMessage(\"Expected referenceLegacyType to be called \$times times, but was called \$referenceLegacyTypeCalls times\").isEqualTo(times)")
                    }
                ))
                .emptyLine()
                .add(Function(
                    name = "assertReferenceLegacyTypeCalledForArgs",
                    args = listOf(
                        Pair("args", "com.some.pkg.legacy.LegacyType"),
                        Pair("times", "Int = 1")
                    ),
                    body = block {
                        line("val calls = referenceLegacyTypeCalls.filter { it == args }")
                        line("assertThat(calls.size).withFailMessage(\"Expected referenceLegacyType to be called \$times times, but was called \$referenceLegacyTypeCalls times\").isEqualTo(times)")
                    }
                ))
                .untab()
                .line("}")
                .build()
        }

        fun contextModule(): String {
            return CodeBuilder()
                .line(ClassDeclaration("${moduleName}Mocks", "ContextModule"))
                .tab()
                .add(Function(
                    override = true,
                    name = "apply",
                    args = listOf(Pair("builder", "ContextBuilder")),
                    body = block {
                        line("builder")
                        .tab()
                        .line(".setImpl($interfaceName::class.java, ${interfaceName}Mock::class.java)")
                        .untab()
                    }
                ))
                .untab()
                .line("}")
                .build()
        }
    }
    override fun generateFileContent(): FileContent? {
        if(c.module.getInterfaces().none { it.getName() == "SomeInterface2" }) {
            return null
        }
        return contentBuilder("mocks.vm")
            .put("view", View("SomeInterface2", "SomeModule"))
            .build()
    }
}