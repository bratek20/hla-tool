package pl.bratek20.hla.velocity.api

interface VelocityFacade {
    fun contentBuilder(templatePath: String): VelocityFileContentBuilder
}