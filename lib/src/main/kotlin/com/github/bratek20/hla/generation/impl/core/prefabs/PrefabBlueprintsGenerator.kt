package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.FileName

class PrefabBlueprintsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PrefabBlueprints
    }

    override fun generateFiles(): List<File> {
        return listOf(
            File.create(
                name = FileName("OtherClass.json"),
                content = FileContent.fromString("""
                    {
                      "name": "OtherClass",
                      "viewType": "OtherModule.View.OtherClassView",
                      "children": [
                        {
                          "name": "id",
                          "viewType": "B20.Frontend.Elements.View.LabelView"
                        },
                        {
                          "name": "amount",
                          "viewType": "B20.Frontend.Elements.View.LabelView"
                        }
                      ]
                    }
                """.trimIndent())
            )
        )
    }
}