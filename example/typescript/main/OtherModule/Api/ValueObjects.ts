// DO NOT EDIT! Autogenerated by HLA tool

class OtherId {
    constructor(
        public readonly value: number
    ) {}

    equals(other: OtherId): boolean {
        return this.value === other.value
    }

    toString(): string {
        return this.value.toString()
    }

    plus(other: OtherId): OtherId {
        return new OtherId(this.value + other.value);
    }

    minus(other: OtherId): OtherId {
        return new OtherId(this.value - other.value);
    }

    times(amount: number): OtherId {
        return new OtherId(this.value * amount);
    }
}

class OtherProperty {
    private id = NUMBER
    private name = STRING

    static create(
        id: OtherId,
        name: string,
    ): OtherProperty {
        const instance = new OtherProperty()
        instance.id = id.value
        instance.name = name
        return instance
    }

    getId(): OtherId {
        return new OtherId(this.id)
    }

    getName(): string {
        return this.name
    }
}

class OtherClass {
    private id = NUMBER
    private amount = NUMBER

    static create(
        id: OtherId,
        amount: number,
    ): OtherClass {
        const instance = new OtherClass()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): OtherId {
        return new OtherId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class OtherHandlerInput {
    private id = NUMBER

    static create(
        id: OtherId,
    ): OtherHandlerInput {
        const instance = new OtherHandlerInput()
        instance.id = id.value
        return instance
    }

    getId(): OtherId {
        return new OtherId(this.id)
    }
}

class OtherHandlerOutput {
    private id = NUMBER

    static create(
        id: OtherId,
    ): OtherHandlerOutput {
        const instance = new OtherHandlerOutput()
        instance.id = id.value
        return instance
    }

    getId(): OtherId {
        return new OtherId(this.id)
    }
}