// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;

namespace OtherModule.Api {
    public class OtherId: ValueObject {
        public int Value { get; }

        public OtherId(
            int value
        ) {
            Value = value;
        }
    }

    public class OtherProperty: ValueObject {
        readonly int id;
        readonly string name;

        public OtherProperty(
            int id,
            string name
        ) {
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

    public class OtherClass: ValueObject {
        readonly int id;
        readonly int amount;

        public OtherClass(
            int id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public OtherId GetId() {
            return new OtherId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static OtherClass Create(OtherId id, int amount) {
            return new OtherClass(id.Value, amount);
        }
    }
}