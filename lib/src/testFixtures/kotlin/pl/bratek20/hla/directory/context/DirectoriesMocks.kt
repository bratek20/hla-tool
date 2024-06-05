package pl.bratek20.hla.directory.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.directory.fixtures.DirectoriesMock
import pl.bratek20.hla.directory.api.Directories
import pl.bratek20.hla.directory.api.Files
import pl.bratek20.hla.directory.fixtures.FilesMock

class DirectoriesMocks: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(Directories::class.java, DirectoriesMock::class.java)
            .setImpl(Files::class.java, FilesMock::class.java)
    }
}