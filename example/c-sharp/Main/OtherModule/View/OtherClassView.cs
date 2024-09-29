using B20.Frontend.Elements.View;
using UnityEngine;
using OtherModule.ViewModel;

namespace OtherModule.View
{
    public class OtherClassView: ElementView<OtherClassVm>
    {
        [SerializeField]
        private LabelView id;
        [SerializeField]
        private LabelView amount;

        protected override void OnBind()
        {
            base.OnBind();
            id.Bind(ViewModel.Id);
            amount.Bind(ViewModel.Amount);
        }
    }
}