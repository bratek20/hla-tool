package com.github.bratek20.hla.types.api

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.SubmoduleName

class HlaTypePath(
    val value: String
){
    fun replaceSubmodule(submodule: SubmoduleName): HlaTypePath {
        return HlaTypePath(value.replaceAfterLast("/", submodule.name))
    }

    companion object {
        fun create(module: ModuleName, submodule: SubmoduleName): HlaTypePath {
            return HlaTypePath("${module.value}/${submodule.name}")
        }
    }
}