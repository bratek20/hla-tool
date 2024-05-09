package pl.bratek20.somemodule.api

import pl.bratek20.architecture.exceptions.ApiException

class SomeException(message: String) : ApiException(message)

class Some2Exception(message: String) : ApiException(message)