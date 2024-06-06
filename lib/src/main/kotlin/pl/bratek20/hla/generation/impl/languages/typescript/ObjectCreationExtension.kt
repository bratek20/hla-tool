package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class ObjectCreationMapper {
    fun map(type: String): String {
        if (type.contains("[]")) {
            return "[" + map(type.replace("[]", "")) + "]"
        }
        if (type.contains("Optional<")) {
            val innerType = type.replace("Optional<", "").replace(">", "")
            return map(innerType)
        }
        return when (type) {
            "string" -> "STRING"
            "number" -> "NUMBER"
            "boolean" -> "BOOLEAN"
            "any" -> "ANY"
            else -> "new $type"
        }
    }

    fun adjustAssignment(type: String): String {
        if (type.contains("Optional<")) {
            return "?"
        }
        return ""
    }
}
class ObjectCreationExtension: ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        builder.put("oc", ObjectCreationMapper())
    }
}