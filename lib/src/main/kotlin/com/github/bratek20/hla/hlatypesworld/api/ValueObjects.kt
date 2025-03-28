// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.hlatypesworld.api

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.generation.api.*
import com.github.bratek20.hla.parsing.api.*
import com.github.bratek20.hla.typesworld.api.*

data class IdSourceInfo(
    private val type: WorldType,
    private val fieldName: String,
    private val parent: WorldType,
) {
    fun getType(): WorldType {
        return this.type
    }

    fun getFieldName(): String {
        return this.fieldName
    }

    fun getParent(): WorldType {
        return this.parent
    }

    companion object {
        fun create(
            type: WorldType,
            fieldName: String,
            parent: WorldType,
        ): IdSourceInfo {
            return IdSourceInfo(
                type = type,
                fieldName = fieldName,
                parent = parent,
            )
        }
    }
}

data class UniqueIdInfo(
    private val type: WorldType,
    private val fieldName: String,
    private val parent: WorldType,
) {
    fun getType(): WorldType {
        return this.type
    }

    fun getFieldName(): String {
        return this.fieldName
    }

    fun getParent(): WorldType {
        return this.parent
    }

    companion object {
        fun create(
            type: WorldType,
            fieldName: String,
            parent: WorldType,
        ): UniqueIdInfo {
            return UniqueIdInfo(
                type = type,
                fieldName = fieldName,
                parent = parent,
            )
        }
    }
}