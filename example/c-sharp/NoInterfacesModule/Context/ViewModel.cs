// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.Architecture.Contexts.Api;
using B20.ViewModel.Windows.Api;
using B20.ViewModel.Popups.Api;
using NoInterfacesModule.Api;
using NoInterfacesModule.ViewModel;

namespace NoInterfacesModule.Context {
    public class NoInterfacesModuleViewModel: ContextModule {
        public void Apply(ContextBuilder builder) {
            builder
                .AddImpl<Popup, NoInterfacesPopup>();
        }
    }
}