// DO NOT EDIT! Autogenerated by HLA tool

class SomeException extends ApiException<SomeException> {
    constructor(
        message: string = "",
    ) {
        super(SomeException, message)
    }
    getTypeName(): string {
        return "SomeException"
    }
}

ExceptionsRegistry.register(SomeException)

class Some2Exception extends ApiException<Some2Exception> {
    constructor(
        message: string = "",
    ) {
        super(Some2Exception, message)
    }
    getTypeName(): string {
        return "Some2Exception"
    }
}

ExceptionsRegistry.register(Some2Exception)