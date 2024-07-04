package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    class CodeBuilder(
        indent: Int = 0
    ) {
        private var currentIndent = indent
        private val lines = mutableListOf<String>()

        fun line(value: String): CodeBuilder {
            val indent = " ".repeat(currentIndent)
            lines.add(indent + value)
            return this
        }

        fun tab(): CodeBuilder {
            currentIndent += 4
            return this
        }

        fun untab(): CodeBuilder {
            currentIndent -= 4
            return this
        }

        fun build(): String {
            return lines.joinToString("\n")
        }
    }
    class View(
        val interfaceName: String
    ) {
        fun block(): String {
            return CodeBuilder()
                .line("class ${interfaceName}Mock: ${interfaceName} {")
                .tab()
                .line("// referenceOtherClass")
                .line("private var referenceOtherClassCalls = mutableListOf<OtherClass>()")
                .line("private val referenceOtherClassResponses = mutableListOf<Pair<ExpectedOtherClass.() -> Unit, OtherClassDef.() -> Unit>>()")
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