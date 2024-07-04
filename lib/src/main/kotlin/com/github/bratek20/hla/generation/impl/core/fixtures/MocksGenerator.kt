package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    interface CodeLineBuilder {
        fun build(): String
    }

    class ListFieldDeclaration(
        private val fieldName: String,
        private val fieldElementType: String
    ): CodeLineBuilder {
        override fun build(): String {
            return "private val $fieldName = mutableListOf<$fieldElementType>()"
        }
    }

    class ClassDeclaration(
        private val className: String,
        private val implementedInterfaceName: String
    ): CodeLineBuilder {
        override fun build(): String {
            return "class $className: $implementedInterfaceName {"
        }

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

        fun line(value: CodeLineBuilder): CodeBuilder {
            return line(value.build())
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
                .line(ClassDeclaration("${interfaceName}Mock", interfaceName))
                .tab()
                .line("// referenceOtherClass")
                .line(ListFieldDeclaration("referenceOtherClassCalls", "OtherClass"))
                .line(ListFieldDeclaration("referenceOtherClassResponses", "Pair<ExpectedOtherClass.() -> Unit, OtherClassDef.() -> Unit>"))
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