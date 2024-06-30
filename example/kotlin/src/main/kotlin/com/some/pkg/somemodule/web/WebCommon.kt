// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.web

import com.some.pkg.somemodule.api.*

import com.some.pkg.othermodule.api.*
import com.some.pkg.typesmodule.api.*

class SomeModuleWebServerUrl(val value: String)

class SomeInterfaceSomeCommandRequest(val id: SomeId, val amount: Int)
class SomeInterfaceSomeQueryRequest(val id: SomeId)
class SomeInterfaceOptMethodRequest(val optId: SomeId?)
class SomeInterface2ReferenceOtherClassRequest(val other: OtherClass)
class SomeInterface2ReferenceLegacyTypeRequest(val legacyType: com.some.pkg.legacy.LegacyType)

class SomeInterfaceSomeQueryResponse(val value: SomeClass)
class SomeInterfaceOptMethodResponse(val value: SomeClass?)
class SomeInterface2ReferenceOtherClassResponse(val value: OtherClass)
class SomeInterface2ReferenceLegacyTypeResponse(val value: com.some.pkg.legacy.LegacyType)
