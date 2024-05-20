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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

        var operationName = args[0];
        var moduleName = new ModuleName(args[1]);
        var language = ModuleLanguage.valueOf(args[2]);
        var hlaFolderPath = new Path(args[3]);
        var projectPath = new Path(args[4]);

        List<String> onlyParts = new ArrayList<>();
        if (args.length > 5) {
            var onlyPartsString = args[5];
            onlyParts = Stream.of(onlyPartsString.split(",")).toList();
        }

        var operationArgs = new ModuleOperationArgs(
            moduleName,
            language,
            hlaFolderPath,
            projectPath,
            onlyParts
        );

        switch (operationName) {
            case "start":
                facade.startModule(operationArgs);
                break;
            case "update":
                facade.updateModule(operationArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operationName);
        }
    }
}
