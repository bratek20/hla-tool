// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.ViewModel.Windows.Api;
using B20.ViewModel.Popups.Api;
using NoInterfacesModule.Api;

namespace NoInterfacesModule.ViewModel {
    public class NoInterfacesPopupState {
        public NoInterfaceId Id { get; }

        public NoInterfacesPopupState(
            NoInterfaceId id
        ) {
            Id = id;
        }
    }

    public partial class NoInterfacesPopup: Popup<NoInterfacesPopupState> {
    }
}