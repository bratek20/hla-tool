package com.github.bratek20.hla.type.impl

import com.github.bratek20.hla.type.api.*

class TypeApiLogic: TypeApi {
    override fun getTypeDependencies(type: HlaType): List<HlaType> {
        return emptyList()
    }
}