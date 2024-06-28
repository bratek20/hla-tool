package com.github.bratek20.hla.parsing.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.parsing.api.ModuleGroupParser

class ParsingContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleGroupParser::class.java, ModuleGroupParserLogic::class.java)
    }
}