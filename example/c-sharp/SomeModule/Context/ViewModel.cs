// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using B20.Ext;
using B20.Architecture.Contexts.Api;
using B20.Frontend.Windows.Api;
using SomeModule.Api;
using SomeModule.ViewModel;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Context {
    public class SomeModuleViewModel: ContextModule {
        public void Apply(ContextBuilder builder) {
            builder
                .SetClass<SomeClassVm>(InjectionMode.Prototype)
                .SetClass<SomeClass2Vm>(InjectionMode.Prototype)
                .SetClass<SomeClass3Vm>(InjectionMode.Prototype)
                .SetClass<SomeClass6Vm>(InjectionMode.Prototype)
                .SetClass<ClassHavingOptSimpleVoVm>(InjectionMode.Prototype)
                .SetClass<ClassWithEnumListVm>(InjectionMode.Prototype)
                .SetClass<SomeClass2VmGroup>(InjectionMode.Prototype)
                .SetClass<SomeEnumSwitchGroup>(InjectionMode.Prototype)
                .SetClass<SomeClassVmGroup>(InjectionMode.Prototype)
                .SetClass<OptionalSomeClassVm>(InjectionMode.Prototype)
                .SetClass<SomeEnumSwitch>(InjectionMode.Prototype)
                .AddImpl<Window, SomeWindow>();
        }
    }
}