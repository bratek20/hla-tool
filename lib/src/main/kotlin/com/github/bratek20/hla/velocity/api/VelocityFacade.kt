package com.github.bratek20.hla.velocity.api

import com.github.bratek20.architecture.exceptions.ApiException

class TemplateNotFoundException(message: String) : ApiException(message)

interface VelocityFacade {
    fun contentBuilder(templatePath: String): VelocityFileContentBuilder
}