package pl.bratek20.hla.parsing.impl

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.parsing.api.ModuleDefinitionsParser

class ParsingContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleDefinitionsParser::class.java, ModuleDefinitionsParserImpl::class.java)
    }
}