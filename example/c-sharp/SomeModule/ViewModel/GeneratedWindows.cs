// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.Frontend.Windows.Api;
using B20.Frontend.UiElements;
using SomeModule.Api;
using OtherModule.Api;
using OtherModule.ViewModel;
using TypesModule.Api;

namespace SomeModule.ViewModel {
    public class SomeWindowState {
        public SomeId SomeId { get; }

        public SomeWindowState(
            SomeId someId
        ) {
            SomeId = someId;
        }
    }

    public partial class SomeWindow: Window<SomeWindowState> {
        public SomeClassVm SomeClassVm { get; set; }
        public SomeClassVmGroup SomeClassVmList { get; set; }
        public Button SomeButton { get; set; }
        public SomeClass6Vm NewOptVm { get; set; }
    }
}