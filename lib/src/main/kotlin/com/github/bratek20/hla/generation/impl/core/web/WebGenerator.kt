package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebClientGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebCommonGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebServerGenerator


class WebGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Web
    }

    override fun velocityDirPath(): String {
        return "web"
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return c.module.getWebSubmodule() != null
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            WebCommonGenerator(),
            WebClientGenerator(),
            WebServerGenerator(),
            PlayFabHandlersGenerator()
        )
    }
}