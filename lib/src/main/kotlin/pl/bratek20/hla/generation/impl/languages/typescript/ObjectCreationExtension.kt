package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class ObjectCreationMapper {
    fun map(type: String): String {
        if (type.contains("[]")) {
            return "[" + map(type.replace("[]", "")) + "]"
        }
        return when (type) {
            "string" -> "STRING"
            "number" -> "NUMBER"
            "boolean" -> "BOOLEAN"
            else -> "new $type"
        }
    }
}
class ObjectCreationExtension: ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        builder.put("oc", ObjectCreationMapper())
    }
}