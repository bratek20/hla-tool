// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.web

import com.github.bratek20.infrastructure.httpclient.api.HttpClientFactory

import com.some.pkg.somemodule.api.*

import com.some.pkg.othermodule.api.*
import com.some.pkg.typesmodule.api.*

class SomeInterfaceWebClient(
    private val factory: HttpClientFactory,
    private val url: SomeModuleWebServerUrl,
): SomeInterface {
    override fun someEmptyMethod(): Unit {
        factory.create(url.value).post("/someInterface/someEmptyMethod", null)
    }

    override fun someCommand(id: SomeId, amount: Int): Unit {
        factory.create(url.value).post("/someInterface/someCommand", SomeInterfaceSomeCommandRequest(id, amount))
    }

    override fun someQuery(query: SomeQueryInput): SomeClass {
        return factory.create(url.value).post("/someInterface/someQuery", SomeInterfaceSomeQueryRequest(query)).getBody(SomeInterfaceSomeQueryResponse::class.java).value
    }

    override fun optMethod(optId: SomeId?): SomeClass? {
        return factory.create(url.value).post("/someInterface/optMethod", SomeInterfaceOptMethodRequest(optId)).getBody(SomeInterfaceOptMethodResponse::class.java).value
    }
}

class SomeInterface2WebClient(
    private val factory: HttpClientFactory,
    private val url: SomeModuleWebServerUrl,
): SomeInterface2 {
    override fun referenceOtherClass(other: OtherClass): OtherClass {
        return factory.create(url.value).post("/someInterface2/referenceOtherClass", SomeInterface2ReferenceOtherClassRequest(other)).getBody(SomeInterface2ReferenceOtherClassResponse::class.java).value
    }

    override fun referenceLegacyType(legacyType: com.some.pkg.legacy.LegacyType): com.some.pkg.legacy.LegacyType {
        return factory.create(url.value).post("/someInterface2/referenceLegacyType", SomeInterface2ReferenceLegacyTypeRequest(legacyType)).getBody(SomeInterface2ReferenceLegacyTypeResponse::class.java).value
    }
}

