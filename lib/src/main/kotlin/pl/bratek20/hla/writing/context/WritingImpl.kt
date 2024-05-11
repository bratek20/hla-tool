package pl.bratek20.hla.writing.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.impl.ModuleWriterLogic

class WritingImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleWriter::class.java, ModuleWriterLogic::class.java)
    }
}