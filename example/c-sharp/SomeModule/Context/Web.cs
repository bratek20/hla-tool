namespace SomeModule.Context
{
    public class SomeModuleWebClient : ContextModule
    {
        private readonly HttpClientConfig config;

        public SomeModuleWebClient(HttpClientConfig config)
        {
            this.config = config;
        }

        public void Apply(ContextBuilder builder)
        {
            builder
                .SetImplObject<SomeModuleWebClientConfig>(new SomeModuleWebClientConfig(_config))
                .SetImpl<SomeInterface, SomeInterfaceWebClient>()
                .SetImpl<SomeInterface2, SomeInterface2WebClient>();
        }
    }
}