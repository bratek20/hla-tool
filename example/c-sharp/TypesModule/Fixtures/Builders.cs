// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using TypesModule.Api;

namespace TypesModule.Fixtures {
    public class TypesModuleBuilders {
        public class DateRangeDef {
            public string From { get; set; } = "01/01/1970 00:00";
            public string To { get; set; } = "01/01/2030 00:00";
        }
        public static Date BuildDate(string value = "01/01/1970 00:00") {
            return TODO(value);
        }
        public static DateRange BuildDateRange(Action<DateRangeDef> init = () => {}) {
            var def = new DateRangeDef();
            init.Invoke(def);
            return DateRange.Create(TODO(def.From), TODO(def.To));
        }
    }
}