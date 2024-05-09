class SomeException extends ApiException<SomeException> {
    constructor(message?: string) {
        super(SomeException, message)
    }

    getType(): string {
        return "SomeException"
    }
}

class Some2Exception extends ApiException<Some2Exception> {
    constructor(message?: string) {
        super(Some2Exception, message)
    }

    getType(): string {
        return "Some2Exception"
    }
}