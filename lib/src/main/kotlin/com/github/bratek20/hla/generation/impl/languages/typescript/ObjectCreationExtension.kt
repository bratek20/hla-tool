package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder

class ObjectCreationMapper {
    fun map(type: String): String {
        return map(type, false)
    }

    private fun map(type: String, isOptional: Boolean): String {
        if (type.contains("[]")) {
            return "[" + map(type.replace("[]", "")) + "]"
        }
        if (type.endsWith("?")) {
            val innerType = type.replace("?", "")
            return map(innerType, true)
        }
        val prefix = if (isOptional) "OPTIONAL_" else ""
        return when (type) {
            "string" -> prefix + "STRING"
            "number" -> prefix + "NUMBER"
            "boolean" -> prefix + "BOOLEAN"
            "any" -> prefix + "ANY"
            else -> {
                if(isOptional) {
                    "OptionalClass($type)"
                }
                else {
                    "new $type"
                }
            }
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