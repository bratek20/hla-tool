class SomeId {
    constructor(
        public readonly value: string
    ) {}
}

class SomeClass {
    constructor(
        public readonly id: SomeId,
        public readonly amount: number,
    ) {}
}

class SomeClass2 {
    constructor(
        public readonly id: SomeId,
        public readonly enabled: boolean,
    ) {}
}
