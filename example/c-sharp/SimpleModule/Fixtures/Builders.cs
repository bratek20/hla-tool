// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using SimpleModule.Api;

namespace SimpleModule.Fixtures {
    public class UniqueIdEntryDef {
        public string Id { get; set; } = "someValue";
    }

    public class SimpleModuleBuilders {
        public static SimpleId BuildSimpleId(string value = "someValue") {
            return new SimpleId(value);
        }
        public static SomeLongWrapper BuildSomeLongWrapper(long value = 0) {
            return new SomeLongWrapper(value);
        }
        public static UniqueIdEntry BuildUniqueIdEntry(Action<UniqueIdEntryDef> init = null) {
            var def = new UniqueIdEntryDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return UniqueIdEntry.Create(def.Id);
        }
    }
}