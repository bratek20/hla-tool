// DO NOT EDIT! Autogenerated by HLA tool

using B20.Ext;

namespace OtherModule.Api {
    public class OtherId {
        public OtherId(
            int value
        ) {
            Value = value;
        }
        public int Value { get; }
    }

    public class OtherProperty {
        private int id;
        private string name;

        public OtherProperty(int id, string name) {
            this.id = id;
            this.name = name;
        }

        public OtherId GetId() {
            return new OtherId(id);
        }

        public string GetName() {
            return name;
        }

        public static OtherProperty Create(OtherId id, string name) {
            return new OtherProperty(id.Value, name);
        }
    }

    public class OtherClass {
        private int id;
        private int amount;

        public OtherClass(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        public OtherId GetId() {
            return new OtherId(Id);
        }

        public int GetAmount() {
            return amount;
        }

        public static OtherClass Create(OtherId id, int amount) {
            return new OtherClass(id.Value, amount);
        }
    }
}