namespace OtherModule {
    export const OTHER_PROPERTY_KEY = new PropertyKey("otherProperty")

    export class OtherProperty {
        private id = STRING
        name = STRING

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
    }
}