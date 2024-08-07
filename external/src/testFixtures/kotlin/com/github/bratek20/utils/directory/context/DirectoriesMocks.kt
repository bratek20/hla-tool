package com.github.bratek20.utils.directory.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.utils.directory.fixtures.DirectoriesMock
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Files
import com.github.bratek20.utils.directory.fixtures.FilesMock

class DirectoriesMocks: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(Directories::class.java, DirectoriesMock::class.java)
            .setImpl(Files::class.java, FilesMock::class.java)
    }
}