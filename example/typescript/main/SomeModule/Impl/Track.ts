// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Impl {
    export class SomeDimension extends TrackingDimension {
        private readonly name: string
        private readonly amount: int
        private readonly date_range: SerializedDateRange
        getTableName(): TrackingTableName {
            return new TrackingTableName("some_dimension")
        }
    }

    export class SomeTrackingEvent extends TrackingEvent {
        private readonly some_dimension_id: SomeDimension
        getTableName(): TrackingTableName {
            return new TrackingTableName("some_tracking_event")
        }
    }
}