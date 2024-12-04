package com.github.bratek20.hla.attributes

import com.github.bratek20.hla.definitions.api.Attribute

class KnownAttribute {
    companion object {
        const val ID_SOURCE = "idSource"
    }
}

fun hasAttribute(attributes: List<Attribute>, name: String): Boolean {
    return attributes.any { it.getName() == name }
}