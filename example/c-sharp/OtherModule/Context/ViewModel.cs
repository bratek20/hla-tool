using B20.Architecture.Contexts.Api;
using B20.Frontend.Windows.Api;
using OtherModule.ViewModel;

namespace OtherModule.Context
{
    public class OtherModuleViewModel: ContextModule
    {
        public void Apply(ContextBuilder builder)
        {
            builder
                .AddImpl<Window, GamesManagementWindow>()
                .SetClass<CreatedGameVmGroup>(InjectionMode.Prototype)
                .SetClass<CreatedGameVm>(InjectionMode.Prototype);
        }
    }
}