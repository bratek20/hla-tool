class SomeException extends ApiException<SomeException> {
    constructor() {
        super(SomeException)
    }

    getType(): string {
        return "SomeException"
    }
}

class Some2Exception extends ApiException<Some2Exception> {
    constructor() {
        super(Some2Exception)
    }

    getType(): string {
        return "Some2Exception"
    }
}