// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.tracking.fixtures

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.definitions.fixtures.*

import com.github.bratek20.hla.tracking.api.*

data class TableDefinitionDef(
    var name: String = "someValue",
    var attributes: List<(AttributeDef.() -> Unit)> = emptyList(),
    var exposedClasses: List<(DependencyConceptDefinitionDef.() -> Unit)> = emptyList(),
    var fields: List<(FieldDefinitionDef.() -> Unit)> = emptyList(),
)
fun tableDefinition(init: TableDefinitionDef.() -> Unit = {}): TableDefinition {
    val def = TableDefinitionDef().apply(init)
    return TableDefinition.create(
        name = def.name,
        attributes = def.attributes.map { it -> attribute(it) },
        exposedClasses = def.exposedClasses.map { it -> dependencyConceptDefinition(it) },
        fields = def.fields.map { it -> fieldDefinition(it) },
    )
}

data class TrackingSubmoduleDefinitionDef(
    var dimensions: List<(TableDefinitionDef.() -> Unit)> = emptyList(),
    var events: List<(TableDefinitionDef.() -> Unit)> = emptyList(),
)
fun trackingSubmoduleDefinition(init: TrackingSubmoduleDefinitionDef.() -> Unit = {}): TrackingSubmoduleDefinition {
    val def = TrackingSubmoduleDefinitionDef().apply(init)
    return TrackingSubmoduleDefinition.create(
        dimensions = def.dimensions.map { it -> tableDefinition(it) },
        events = def.events.map { it -> tableDefinition(it) },
    )
}