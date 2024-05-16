namespace OtherModule {
    export const OTHER_PROPERTY_KEY = new PropertyKey("otherProperty")
}

class OtherProperty {
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

    getId(): OtherId {
        return new OtherId(this.id)
    }
}