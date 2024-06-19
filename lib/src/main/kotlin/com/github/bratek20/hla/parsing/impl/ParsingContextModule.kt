package com.github.bratek20.hla.parsing.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.parsing.api.ModuleDefinitionsParser

class ParsingContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleDefinitionsParser::class.java, ModuleDefinitionsParserLogic::class.java)
    }
}