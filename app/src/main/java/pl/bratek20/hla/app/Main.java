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
import java.util.Collections;
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
                List.of(
                    new HlaProfile(
                        "hla",
                        ModuleLanguage.KOTLIN,
                        "../lib",
                        "src/main/kotlin/pl/bratek20/hla",
                        "src/testFixtures/kotlin/pl/bratek20/hla",
                        Collections.emptyList(),
                        false
                    ),
                    new HlaProfile(
                        "PlayFab",
                        ModuleLanguage.TYPE_SCRIPT,
                        "../lib",
                        "src/main/kotlin/pl/bratek20/hla",
                        "src/testFixtures/kotlin/pl/bratek20/hla",
                        Collections.emptyList(),
                        false
                    ),
                    new HlaProfile(
                        "WohProperties",
                        ModuleLanguage.KOTLIN,
                        "../woh-properties",
                        "src/main/java/com/rortos/woh",
                        "src/testFixtures/java/com/rortos/woh",
                        List.of(
                            "NamedTypes",
                            "CustomTypes",
                            "CustomTypesMapper",
                            "Properties",
                            "Builders"
                        ),
                        false
                    )
                )
            )
        );

        var facade = context.get(HlaFacade.class);

        var operationName = args[0];
        var hlaFolderPath = new Path(args[1]);
        var profileName = new ProfileName(args[2]);
        var moduleName = new ModuleName(args[3]);

        System.out.println("Operation: " + operationName + ", HLA folder path: " + hlaFolderPath + ", profile name: " + profileName + ", module name: " + moduleName);

        var operationArgs = new ModuleOperationArgs(
            hlaFolderPath,
            profileName,
            moduleName
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
