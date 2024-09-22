using System;
using System.Collections.Generic;
using B20.Frontend.Traits;
using B20.Frontend.UiElements;
using OtherModule.Api;

namespace OtherModule.ViewModel
{
    public partial class OtherClassVm: UiElement<OtherClass>
    {
        public Label Id { get; set; }
        public Label Amount { get; set; }

        protected override void OnUpdate()
        {
            Id.Update(Model.GetId().Value);
            Amount.Update(Model.GetAmount());
        }
    }
}