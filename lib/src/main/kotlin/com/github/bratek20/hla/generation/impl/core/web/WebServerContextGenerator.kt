package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.FileContent

class WebServerContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebServerContext
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }
        return c.module.getWebSubmodule()?.let { web ->
            contentBuilder("webServerContext.vm")
                .put("interfaceNames", web.getHttp()!!.getExposedInterfaces())
                .build()
        }
    }
}