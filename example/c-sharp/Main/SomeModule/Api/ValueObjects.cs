// DO NOT EDIT! Autogenerated by HLA tool

using B20.Ext;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Api {
    public class SomeId {
        public string Value { get; }

        public SomeId(
            string value
        ) {
            Value = value;
        }
    }

    public class SomeIntWrapper {
        public int Value { get; }

        public SomeIntWrapper(
            int value
        ) {
            Value = value;
        }
    }

    public class SomeId2 {
        public int Value { get; }

        public SomeId2(
            int value
        ) {
            Value = value;
        }
    }

    public class SomeClass {
        readonly string id;
        readonly int amount;

        public SomeClass(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeClass Create(SomeId id, int amount) {
            return new SomeClass(id.Value, amount);
        }
    }

    public class SomeClass2 {
        readonly string id;
        readonly string[] names;
        readonly string[] ids;
        readonly bool enabled;

        public SomeClass2(
            string id,
            string[] names,
            string[] ids,
            bool enabled
        ) {
            this.id = id;
            this.names = names;
            this.ids = ids;
            this.enabled = enabled;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public string[] GetNames() {
            return names;
        }
        public SomeId[] GetIds() {
            return ids.Select( it => new SomeId(it) );
        }
        public bool GetEnabled() {
            return enabled;
        }
        public static SomeClass2 Create(SomeId id, string[] names, SomeId[] ids, bool enabled) {
            return new SomeClass2(id.Value, names, ids.Select( it => it.Value ), enabled);
        }
    }

    public class SomeClass3 {
        readonly SomeClass2 class2Object;
        readonly string someEnum;
        readonly SomeClass2[] class2List;

        public SomeClass3(
            SomeClass2 class2Object,
            string someEnum,
            SomeClass2[] class2List
        ) {
            this.class2Object = class2Object;
            this.someEnum = someEnum;
            this.class2List = class2List;
        }
        public SomeClass2 GetClass2Object() {
            return class2Object;
        }
        public SomeEnum GetSomeEnum() {
            return SomeEnum.fromName(someEnum).get();
        }
        public SomeClass2[] GetClass2List() {
            return class2List;
        }
        public static SomeClass3 Create(SomeClass2 class2Object, SomeEnum someEnum, SomeClass2[] class2List) {
            return new SomeClass3(class2Object, someEnum.getName(), class2List);
        }
    }

    public class SomeClass4 {
        readonly int otherId;
        readonly OtherClass otherClass;
        readonly int[] otherIdList;
        readonly OtherClass[] otherClassList;

        public SomeClass4(
            int otherId,
            OtherClass otherClass,
            int[] otherIdList,
            OtherClass[] otherClassList
        ) {
            this.otherId = otherId;
            this.otherClass = otherClass;
            this.otherIdList = otherIdList;
            this.otherClassList = otherClassList;
        }
        public OtherId GetOtherId() {
            return new OtherId(otherId);
        }
        public OtherClass GetOtherClass() {
            return otherClass;
        }
        public OtherId[] GetOtherIdList() {
            return otherIdList.Select( it => new OtherId(it) );
        }
        public OtherClass[] GetOtherClassList() {
            return otherClassList;
        }
        public static SomeClass4 Create(OtherId otherId, OtherClass otherClass, OtherId[] otherIdList, OtherClass[] otherClassList) {
            return new SomeClass4(otherId.Value, otherClass, otherIdList.Select( it => it.Value ), otherClassList);
        }
    }

    public class SomeClass5 {
        readonly string date;
        readonly SerializedDateRange dateRange;
        readonly SerializedDateRangeWrapper dateRangeWrapper;
        readonly SomeProperty someProperty;
        readonly OtherProperty otherProperty;

        public SomeClass5(
            string date,
            SerializedDateRange dateRange,
            SerializedDateRangeWrapper dateRangeWrapper,
            SomeProperty someProperty,
            OtherProperty otherProperty
        ) {
            this.date = date;
            this.dateRange = dateRange;
            this.dateRangeWrapper = dateRangeWrapper;
            this.someProperty = someProperty;
            this.otherProperty = otherProperty;
        }
        public Date GetDate() {
            return TODO(date);
        }
        public DateRange GetDateRange() {
            return dateRange.toCustomType();
        }
        public DateRangeWrapper GetDateRangeWrapper() {
            return dateRangeWrapper.toCustomType();
        }
        public SomeProperty GetSomeProperty() {
            return someProperty;
        }
        public OtherProperty GetOtherProperty() {
            return otherProperty;
        }
        public static SomeClass5 Create(Date date, DateRange dateRange, DateRangeWrapper dateRangeWrapper, SomeProperty someProperty, OtherProperty otherProperty) {
            return new SomeClass5(TODO(date), dateRange.fromCustomType(), dateRangeWrapper.fromCustomType(), someProperty, otherProperty);
        }
    }

    public class SomeClass6 {
        readonly Optional<SomeClass> someClassOpt;
        readonly Optional<string> optString;
        readonly SomeClass6[] sameClassList;

        public SomeClass6(
            Optional<SomeClass> someClassOpt,
            Optional<string> optString,
            SomeClass6[] sameClassList
        ) {
            this.someClassOpt = someClassOpt;
            this.optString = optString;
            this.sameClassList = sameClassList;
        }
        public Optional<SomeClass> GetSomeClassOpt() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public Optional<string> GetOptString() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public SomeClass6[] GetSameClassList() {
            return sameClassList;
        }
        public static SomeClass6 Create(Optional<SomeClass> someClassOpt, Optional<string> optString, SomeClass6[] sameClassList) {
            return new SomeClass6(TODO OptionalApiType.modernSerialize, TODO OptionalApiType.modernSerialize, sameClassList);
        }
    }

    public class ClassUsingExternalType {
        readonly LegacyType extType;

        public ClassUsingExternalType(
            LegacyType extType
        ) {
            this.extType = extType;
        }
        public LegacyType GetExtType() {
            return extType;
        }
        public static ClassUsingExternalType Create(LegacyType extType) {
            return new ClassUsingExternalType(extType);
        }
    }

    public class ClassHavingOptList {
        readonly Optional<SomeClass[]> optList;

        public ClassHavingOptList(
            Optional<SomeClass[]> optList
        ) {
            this.optList = optList;
        }
        public Optional<SomeClass[]> GetOptList() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public static ClassHavingOptList Create(Optional<SomeClass[]> optList) {
            return new ClassHavingOptList(TODO OptionalApiType.modernSerialize);
        }
    }

    public class RecordClass {
        readonly string id;
        readonly int amount;

        public RecordClass(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static RecordClass Create(SomeId id, int amount) {
            return new RecordClass(id.Value, amount);
        }
    }

    public class SomeQueryInput {
        readonly string id;
        readonly int amount;

        public SomeQueryInput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeQueryInput Create(SomeId id, int amount) {
            return new SomeQueryInput(id.Value, amount);
        }
    }

    public class SomeHandlerInput {
        readonly string id;
        readonly int amount;

        public SomeHandlerInput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeHandlerInput Create(SomeId id, int amount) {
            return new SomeHandlerInput(id.Value, amount);
        }
    }

    public class SomeHandlerOutput {
        readonly string id;
        readonly int amount;

        public SomeHandlerOutput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeHandlerOutput Create(SomeId id, int amount) {
            return new SomeHandlerOutput(id.Value, amount);
        }
    }

    public class SomeProperty {
        readonly OtherProperty other;
        readonly Optional<int> id2;
        readonly Optional<SerializedDateRange> range;
        readonly double doubleExample;
        readonly long longExample;
        readonly string goodName;
        readonly any customData;

        public SomeProperty(
            OtherProperty other,
            Optional<int> id2,
            Optional<SerializedDateRange> range,
            double doubleExample,
            long longExample,
            string goodName,
            any customData
        ) {
            this.other = other;
            this.id2 = id2;
            this.range = range;
            this.doubleExample = doubleExample;
            this.longExample = longExample;
            this.goodName = goodName;
            this.customData = customData;
        }
        public OtherProperty GetOther() {
            return other;
        }
        public Optional<SomeId2> GetId2() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public Optional<DateRange> GetRange() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public double GetDoubleExample() {
            return doubleExample;
        }
        public long GetLongExample() {
            return longExample;
        }
        public string GetGoodName() {
            return goodName;
        }
        public any GetCustomData() {
            return customData;
        }
        public static SomeProperty Create(OtherProperty other, Optional<SomeId2> id2, Optional<DateRange> range, double doubleExample, long longExample, string goodName, any customData) {
            return new SomeProperty(other, TODO OptionalApiType.modernSerialize, TODO OptionalApiType.modernSerialize, doubleExample, longExample, goodName, customData);
        }
    }

    public class SomeProperty2 {
        readonly string value;
        readonly any custom;
        readonly string someEnum;
        readonly Optional<any> customOpt;

        public SomeProperty2(
            string value,
            any custom,
            string someEnum,
            Optional<any> customOpt
        ) {
            this.value = value;
            this.custom = custom;
            this.someEnum = someEnum;
            this.customOpt = customOpt;
        }
        public string GetValue() {
            return value;
        }
        public any GetCustom() {
            return custom;
        }
        public SomeEnum GetSomeEnum() {
            return SomeEnum.fromName(someEnum).get();
        }
        public Optional<any> GetCustomOpt() {
            return TODO OptionalApiType.modernDeserialize;
        }
        public static SomeProperty2 Create(string value, any custom, SomeEnum someEnum, Optional<any> customOpt) {
            return new SomeProperty2(value, custom, someEnum.getName(), TODO OptionalApiType.modernSerialize);
        }
    }
}