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
        val interfaceName: String
    ) {
        fun block(): String {
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
                .build()
        }
    }
    override fun generateFileContent(): FileContent? {
        if(c.module.getInterfaces().none { it.getName() == "SomeInterface2" }) {
            return null
        }
        return contentBuilder("mocks.vm")
            .put("view", View("SomeInterface2"))
            .build()
    }
}