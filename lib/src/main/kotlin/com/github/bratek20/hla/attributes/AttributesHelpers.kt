package com.github.bratek20.hla.attributes

import com.github.bratek20.hla.definitions.api.Attribute

class KnownAttribute {
    companion object {
        const val ID_SOURCE = "idSource"
        const val UNIQUE = "unique"
    }
}

fun hasAttribute(attributes: List<Attribute>, name: String): Boolean {
    return attributes.any { it.getName() == name }
}

fun getAttributeValue(attributes: List<Attribute>, name: String): String {
    return attributes.first { it.getName() == name }.getValue()
}