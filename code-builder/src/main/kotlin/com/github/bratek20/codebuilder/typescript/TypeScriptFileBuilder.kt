package com.github.bratek20.codebuilder.typescript

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class TypeScriptFileBuilder: FileBuilder() {
}
typealias TypeScriptFileBuilderOps = TypeScriptFileBuilder.() -> Unit
fun CodeBuilder.typeScriptFile(block: TypeScriptFileBuilderOps) = add(TypeScriptFileBuilder().apply(block))