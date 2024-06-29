package com.some.pkg.somemodule.web

import com.some.pkg.somemodule.api.*

data class SomeModuleWebServerUrl(
    val value: String
)

class SomeInterfaceSomeCommandRequest(
    val id: SomeId,
    val amount: Int,
)

class SomeInterfaceSomeQueryRequest(
    val id: SomeId,
)
class SomeInterfaceSomeQueryResponse(
    val value: SomeClass,
)

class SomeInterfaceOptMethodRequest(
    val optId: SomeId?,
)
class SomeInterfaceOptMethodResponse(
    val value: SomeClass?,
)