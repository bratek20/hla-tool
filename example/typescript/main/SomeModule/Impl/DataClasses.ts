// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Impl {
    export class SomeImplData {
        private id = STRING
        private name = STRING

        static create(
            id: SomeId,
            name: string,
        ): SomeImplData {
            const instance = new SomeImplData()
            instance.id = id.value
            instance.name = name
            return instance
        }

        getId(): SomeId {
            return new SomeId(this.id)
        }

        getName(): string {
            return this.name
        }

        setId(id: SomeId): void {
            this.id = id.value
        }

        setName(name: string): void {
            this.name = name
        }

        update(other: SomeImplData) {
            this.id = other.id
            this.name = other.name
        }
    }
}