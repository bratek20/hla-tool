package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

class PrefabJsonGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabJson
    }

    override fun generateFiles(): List<File> {
        return listOf(
            File.create(
                name = FileName("OtherClass.json"),
                content = FileContent.fromString("""
                    {
                      "prefabName": "OtherClass",
                      "viewTypeName": "OtherModule.View.OtherClassView",
                      "children": [
                        {
                          "name": "id",
                          "prefabPath": "OtherModule.Prefabs.OtherClassId"
                        },
                        {
                          "name": "amount",
                          "prefabPath": "OtherModule.Prefabs.OtherClassId"
                        }
                      ]
                    }
                """.trimIndent())
            )
        )
    }
}