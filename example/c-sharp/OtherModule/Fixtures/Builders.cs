// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using OtherModule.Api;

namespace OtherModule.Fixtures {
    public class OtherPropertyDef {
        public int Id { get; set; } = 0;
        public string Name { get; set; } = "someValue";
    }

    public class OtherClassDef {
        public int Id { get; set; } = 0;
        public int Amount { get; set; } = 0;
    }

    public class OtherClassWIthUniqueIdDef {
        public string UniqueId { get; set; } = "someValue";
    }

    public class OtherDataDef {
        public int Id { get; set; } = 0;
    }

    public class OtherModuleBuilders {
        public static OtherId BuildOtherId(int value = 0) {
            return new OtherId(value);
        }
        public static OtherProperty BuildOtherProperty(Action<OtherPropertyDef> init = null) {
            var def = new OtherPropertyDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return OtherProperty.Create(new OtherId(def.Id), def.Name);
        }
        public static OtherClass BuildOtherClass(Action<OtherClassDef> init = null) {
            var def = new OtherClassDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return OtherClass.Create(new OtherId(def.Id), def.Amount);
        }
        public static OtherClassWIthUniqueId BuildOtherClassWIthUniqueId(Action<OtherClassWIthUniqueIdDef> init = null) {
            var def = new OtherClassWIthUniqueIdDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return OtherClassWIthUniqueId.Create(def.UniqueId);
        }
        public static OtherData BuildOtherData(Action<OtherDataDef> init = null) {
            var def = new OtherDataDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return OtherData.Create(new OtherId(def.Id));
        }
    }
}