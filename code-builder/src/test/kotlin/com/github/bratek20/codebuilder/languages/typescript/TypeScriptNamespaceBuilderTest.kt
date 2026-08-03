package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.legacyConstructorCall
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.core.testOp
import com.github.bratek20.codebuilder.types.typeName
import org.junit.jupiter.api.Test

class TypeScriptNamespaceBuilderTest {
    @Test
    fun `should work`() {
        testOp {
            op = {
                add(typeScriptNamespace {
                    name = "SomeNamespace"
                    addClass {
                        name = "SomeClass"
                    }
                    addFunction {
                        name = "someFunction"
                    }
                    addConst {
                        name = "someConst"
                        value = {
                            legacyConstructorCall {
                                className = "SomeClass"
                            }
                        }
                    }
                    addFunctionCall {
                        name = "someFunction"
                    }
                    addVariable {
                        name = "someVariable"
                        type = typeName("SomeType")
                        mutable = true
                    }
                    addInterface {
                        name = "SomeArgs"
                        addField {
                            name = "someField"
                            type = typeName("SomeType")
                            optional = true
                        }
                    }
                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    namespace SomeNamespace {
                        export class SomeClass {
                        }
                    
                        export function someFunction() {
                        }
                    
                        export const someConst = new SomeClass()
                    
                        someFunction()
                    
                        export let someVariable: SomeType
                    
                        export interface SomeArgs {
                            someField?: SomeType
                        }
                    }
                """
            }
        }
    }
}