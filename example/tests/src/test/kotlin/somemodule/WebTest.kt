package somemodule

import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.infrastructure.httpclient.context.HttpClientImpl
import com.github.bratek20.infrastructure.httpserver.api.WebServerModule
import com.github.bratek20.infrastructure.httpserver.fixtures.TestWebApp
import com.some.pkg.somemodule.api.SomeInterface
import com.some.pkg.somemodule.context.SomeModuleWebClient
import com.some.pkg.somemodule.web.SomeInterfaceController

class TestSomeModuleWebServer: WebServerModule {
    override fun getImpl(): ContextModule {
        return TestSomeModuleImpl()
    }

    override fun getControllers(): List<Class<*>> {
        return listOf(
            SomeInterfaceController::class.java,
        )
    }
}

class SomeModuleWebTest: TestSomeModuleImplTest() {
    override fun createSomeInterface(): SomeInterface {
        //server
        val serverPort = TestWebApp(
            modules = listOf(
                TestSomeModuleWebServer()
            )
        ).run().port

        //client
        val someInterface = someContextBuilder()
            .withModules(
                HttpClientImpl(),
                SomeModuleWebClient(
                    serverUrl = "http://localhost:$serverPort"
                )
            )
            .get(SomeInterface::class.java)

        return someInterface
    }
}