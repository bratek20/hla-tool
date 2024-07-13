package com.github.bratek20.utils.directory.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.utils.directory.api.Directories
import com.github.bratek20.utils.directory.api.Files
import com.github.bratek20.utils.directory.impl.DirectoriesLogic
import com.github.bratek20.utils.directory.impl.FilesLogic

class DirectoryImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(Directories::class.java, DirectoriesLogic::class.java)
            .setImpl(Files::class.java, FilesLogic::class.java)
    }
}