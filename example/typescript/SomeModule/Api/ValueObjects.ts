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
        public readonly names: string[],
        public readonly ids: SomeId[],
    ) {}
}

class SomeClass3 {
    constructor(
        public readonly class2Object: SomeClass2,
        public readonly class2List: SomeClass2[],
    ) {}
}

