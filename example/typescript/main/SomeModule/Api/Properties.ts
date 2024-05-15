namespace SomeModule {
    export class SomeProperty {
        other = new OtherProperty

        static create(
            other: OtherProperty,
        ): SomeProperty {
            const instance = new SomeProperty()
            instance.other = other
            return instance
        }
    }
}