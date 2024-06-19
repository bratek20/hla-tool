package pl.bratek20.hla.writing.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.writing.api.ModuleWriter
import pl.bratek20.hla.writing.impl.FilesModifiers
import pl.bratek20.hla.writing.impl.ModuleWriterLogic

class WritingImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setClass(FilesModifiers::class.java)
            .setImpl(ModuleWriter::class.java, ModuleWriterLogic::class.java)
    }
}