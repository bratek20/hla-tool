package pl.bratek20.hla.app;

import pl.bratek20.architecture.context.spring.SpringContextBuilder;
import pl.bratek20.architecture.properties.api.PropertyKey;
import pl.bratek20.architecture.properties.impl.PropertiesModule;
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource;
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSourceModule;
import pl.bratek20.hla.facade.api.*;
import pl.bratek20.hla.directory.api.Path;
import pl.bratek20.hla.directory.context.DirectoryImpl;
import pl.bratek20.hla.facade.context.FacadeImpl;

public class Main {
    public static void main(String[] args) {
        var context = new SpringContextBuilder()
            .withModules(
                new DirectoryImpl(),
                new PropertiesModule(),
                new InMemoryPropertiesSourceModule(),
                new FacadeImpl()
            )
            .build();

        var propertiesSource = context.get(InMemoryPropertiesSource.class);
        propertiesSource.set(
            new PropertyKey("properties"),
            new HlaProperties(
                false,
                new KotlinProperties(
                   "pl.bratek20.hla"
                ),
                new TypeScriptProperties(
                     "Src",
                    "Test/TS"
                )
            )
        );

        var facade = context.get(HlaFacade.class);

        var moduleName = new ModuleName(args[0]);
        var language = ModuleLanguage.valueOf(args[1]);
        var hlaFolderPath = new Path(args[2]);
        var projectPath = new Path(args[3]);

        facade.generateModule(
            new GenerateModuleArgs(
                moduleName,
                language,
                hlaFolderPath,
                projectPath
            )
        );
    }
}
