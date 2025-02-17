package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.builders.returnStatement
import com.github.bratek20.codebuilder.builders.string
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class TrackGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Track
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT &&
                c.module.getTrackingSubmodule() != null
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "SomeDimension"
            extends {
                className = "TrackingDimension"
            }

            addMethod {
                name = "getTableName"
                returnType = typeName("TrackingTableName")
                setBody {
                    add(returnStatement {
                        constructorCall {
                            className = "TrackingTableName"
                            addArg {
                                string("some_dimension")
                            }
                        }
                    })
                }
            }
        }

        addClass {
            name = "SomeTrackingEvent"
            extends {
                className = "TrackingEvent"
            }

            addMethod {
                name = "getTableName"
                returnType = typeName("TrackingTableName")
                setBody {
                    add(returnStatement {
                        constructorCall {
                            className = "TrackingTableName"
                            addArg {
                                string("some_tracking_event")
                            }
                        }
                    })
                }
            }
        }
    }
}