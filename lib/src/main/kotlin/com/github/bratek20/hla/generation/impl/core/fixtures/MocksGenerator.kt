package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.FileGenerator

class MocksGenerator: FileGenerator() {
    override fun name(): String {
        return "Mocks"
    }

    class View {
        fun block(): String {
            val indent = "    "
            val result = "// referenceOtherClass\n" +
            indent + "private var referenceOtherClassCalls = mutableListOf<OtherClass>()\n" +
            indent + "private val referenceOtherClassResponses = mutableListOf<Pair<ExpectedOtherClass.() -> Unit, OtherClassDef.() -> Unit>>()"

            return result
        }
    }
    override fun generateFileContent(): FileContent? {
        if(c.module.getInterfaces().none { it.getName() == "SomeInterface2" }) {
            return null
        }
        return contentBuilder("mocks.vm")
            .put("interfaceName", "SomeInterface2")
            .put("view", View())
            .build()
    }
}