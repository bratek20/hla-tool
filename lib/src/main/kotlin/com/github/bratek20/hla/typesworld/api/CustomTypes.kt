package com.github.bratek20.hla.typesworld.api

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.parsing.api.GroupName

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

        fun create(
            moduleGroup: GroupName,
            module: ModuleName,
            submodule: SubmoduleName,
            pattern: PatternName
        ): HlaTypePath {
            return HlaTypePath("${moduleGroup.value}/${module.value}/${submodule.name}/${pattern.name}")
        }
    }
}