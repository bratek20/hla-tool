// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using B20.Ext;
using B20.Frontend.Windows.Api;
using B20.Frontend.UiElements;
using SomeModule.Api;
using OtherModule.Api;
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
    }
}