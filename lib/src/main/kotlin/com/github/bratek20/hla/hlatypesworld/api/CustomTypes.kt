package com.github.bratek20.hla.hlatypesworld.api

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.typesworld.api.WorldTypePath

class HlaTypePath(
    val value: String
){
    fun replaceSubmoduleAndPattern(submodule: SubmoduleName, pattern: PatternName): HlaTypePath {
        val parts = value.split("/").toMutableList()
        parts[parts.size - 2] = submodule.name
        parts[parts.size - 1] = pattern.name
        return HlaTypePath(parts.joinToString("/"))
    }

    fun dropPatternPart(): String {
        return value.replaceAfterLast("/", "").dropLast(1)
    }

    fun getModuleName(): ModuleName {
        return ModuleName(getNthLastPart(3))
    }

    fun getSubmoduleName(): SubmoduleName {
        return SubmoduleName.valueOf(getNthLastPart(2))
    }

    fun getPatternName(): PatternName {
        return PatternName.valueOf(getNthLastPart(1))
    }

    private fun getNthLastPart(n: Int): String {
        val parts = asWorld().asParts()
        return parts[parts.size - n]
    }

    companion object {
        fun create(
            module: ModuleName,
            submodule: SubmoduleName,
            pattern: PatternName
        ): HlaTypePath {
            return HlaTypePath("${module.value}/${submodule.name}/${pattern.name}");
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