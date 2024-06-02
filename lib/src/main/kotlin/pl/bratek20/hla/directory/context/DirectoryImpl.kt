package pl.bratek20.hla.directory.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Files
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.directory.impl.FilesLogic

class DirectoryImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(Directories::class.java, DirectoriesLogic::class.java)
            .setImpl(Files::class.java, FilesLogic::class.java)
    }
}