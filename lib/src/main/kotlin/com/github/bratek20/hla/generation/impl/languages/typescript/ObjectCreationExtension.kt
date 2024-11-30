package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder

class ObjectCreationMapper {
    fun map(type: String): String {
        if (type.contains("[]")) {
            return "[" + map(type.replace("[]", "")) + "]"
        }
        if (type.endsWith("?")) {
            val innerType = type.replace("?", "")
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
        if (type.endsWith("?")) {
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