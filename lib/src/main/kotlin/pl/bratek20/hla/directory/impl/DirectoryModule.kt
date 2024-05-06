package pl.bratek20.hla.directory.impl

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.directory.api.Directories

class DirectoryModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(Directories::class.java, DirectoriesLogic::class.java)
    }
}