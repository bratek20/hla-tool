package com.github.bratek20.hla.velocity.api

import com.github.bratek20.utils.directory.api.FileContent

interface VelocityFileContentBuilder {
    fun put(key: String, value: Any?): VelocityFileContentBuilder
    fun build(): FileContent
}