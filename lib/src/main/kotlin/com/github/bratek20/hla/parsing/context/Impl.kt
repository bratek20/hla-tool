package com.github.bratek20.hla.parsing.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.parsing.impl.ModuleGroupParserLogic

class ParsingImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(ModuleGroupParser::class.java, ModuleGroupParserLogic::class.java)
    }
}