package com.some.pkg.somemodule.web

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.some.pkg.somemodule.api.*

@RestController
@RequestMapping("/someInterface")
class SomeInterfaceController(
    private val api: SomeInterface,
) {
    @RequestMapping("/someCommand")
    fun someCommand(@RequestBody request: SomeInterfaceSomeCommandRequest) {
        api.someCommand(request.id, request.amount)
    }

    @RequestMapping("/someQuery")
    fun someQuery(@RequestBody request: SomeInterfaceSomeQueryRequest): SomeInterfaceSomeQueryResponse {
        return SomeInterfaceSomeQueryResponse(api.someQuery(request.id))
    }

    @RequestMapping("/optMethod")
    fun optMethod(@RequestBody request: SomeInterfaceOptMethodRequest): SomeInterfaceOptMethodResponse {
        return SomeInterfaceOptMethodResponse(api.optMethod(request.optId))
    }
}