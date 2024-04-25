namespace OtherModule {
    export class OtherProperty {
        private id = STRING
        name = STRING

        static create(
            id: string,
            name: string,
        ): OtherProperty {
            const instance = new OtherProperty()
            instance.id = id
            instance.name = name
            return instance
        }

        getId(): SomeId {
            return new SomeId(this.id)
        }
    }
}