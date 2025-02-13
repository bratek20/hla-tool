// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.prefabcreator.fixtures

import com.github.bratek20.hla.prefabcreator.api.*

data class PrefabChildBlueprintDef(
    var name: String = "someValue",
    var viewType: String = "someValue",
)
fun prefabChildBlueprint(init: PrefabChildBlueprintDef.() -> Unit = {}): PrefabChildBlueprint {
    val def = PrefabChildBlueprintDef().apply(init)
    return PrefabChildBlueprint.create(
        name = def.name,
        viewType = def.viewType,
    )
}

data class PrefabBlueprintDef(
    var blueprintType: String = BlueprintType.ComplexElement.name,
    var name: String = "someValue",
    var viewType: String = "someValue",
    var creationOrder: Int = 0,
    var children: List<(PrefabChildBlueprintDef.() -> Unit)> = emptyList(),
    var elementViewType: String? = null,
)
fun prefabBlueprint(init: PrefabBlueprintDef.() -> Unit = {}): PrefabBlueprint {
    val def = PrefabBlueprintDef().apply(init)
    return PrefabBlueprint.create(
        blueprintType = BlueprintType.valueOf(def.blueprintType),
        name = def.name,
        viewType = def.viewType,
        creationOrder = def.creationOrder,
        children = def.children.map { it -> prefabChildBlueprint(it) },
        elementViewType = def.elementViewType,
    )
}