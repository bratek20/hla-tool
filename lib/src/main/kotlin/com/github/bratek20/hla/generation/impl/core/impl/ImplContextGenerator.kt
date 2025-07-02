package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.utils.directory.api.FileContent

class ImplContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ImplContext
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent? {
        if (module.getInterfaces().isEmpty()) {
            return null
        }

        val factory = InterfaceViewFactory(apiTypeFactory)
        return contentBuilder("implContext.vm")
            .put("interfaces", factory.create(module.getInterfaces()))
            .build()
    }
}