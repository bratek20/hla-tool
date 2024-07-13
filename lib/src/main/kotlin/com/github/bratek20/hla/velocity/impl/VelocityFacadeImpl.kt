package com.github.bratek20.hla.velocity.impl

import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.fileContentFromString
import com.github.bratek20.hla.velocity.api.VelocityFacade
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder
import java.io.StringWriter

class VelocityFileContentBuilderImpl(
    private val template: Template,
) : VelocityFileContentBuilder {
    private val context = VelocityContext()

    override fun put(key: String, value: Any?): VelocityFileContentBuilder {
        context.put(key, value)
        return this
    }

    override fun build(): FileContent {
        val writer = StringWriter()
        template.merge(context, writer)
        return fileContentFromString(writer.toString())
    }
}

class VelocityFacadeImpl: VelocityFacade {
    private val engine: VelocityEngine = VelocityEngine()

    init {
        engine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath")
        engine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader::class.java.name)
        engine.init()
    }

    override fun contentBuilder(templatePath: String): VelocityFileContentBuilder {
        val template = engine.getTemplate(templatePath)
        return VelocityFileContentBuilderImpl(template)
    }
}