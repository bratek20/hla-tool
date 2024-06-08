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
}class OtherProperty {
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