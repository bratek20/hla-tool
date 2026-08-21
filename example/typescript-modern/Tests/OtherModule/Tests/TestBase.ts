import { OTHER_PROPERTIES_PROPERTY_KEY, OTHER_PROPERTY_PROPERTY_KEY } from "../../../main/OtherModule/Api/PropertyKeys"
import * as Builder from "../Fixtures/Builders"

export let c: HandlerContext

export interface SetupArgs {
    otherProperty?: Builder.OtherPropertyDef
    otherProperties?: Builder.OtherPropertyDef[]
}

export function setup(args: SetupArgs = {}): void {
    c = Ts.E2E.SetupAndCreateContext({
        dependencyName: DependencyName.OtherModule,
        titleData: builderTD => {
            builderTD.with(OTHER_PROPERTY_PROPERTY_KEY, Builder.otherProperty(args.otherProperty ?? {}))
            builderTD.with(OTHER_PROPERTIES_PROPERTY_KEY, (args.otherProperties ?? []).map(it => Builder.otherProperty(it)))
        }
    }).context
}
