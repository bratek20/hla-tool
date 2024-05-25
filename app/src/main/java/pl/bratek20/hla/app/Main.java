package pl.bratek20.hla.app;

import pl.bratek20.architecture.context.spring.SpringContextBuilder;
import pl.bratek20.hla.directory.api.Path;
import pl.bratek20.hla.directory.context.DirectoryImpl;
import pl.bratek20.hla.facade.api.*;
import pl.bratek20.hla.facade.context.FacadeImpl;

public class Main {
    public static void main(String[] args) {
        var context = new SpringContextBuilder()
            .withModules(
                new DirectoryImpl(),
                new FacadeImpl()
            )
            .build();

//        var propertiesSource = context.get(InMemoryPropertiesSource.class);
//        propertiesSource.set(
//            new PropertyKey("properties"),
//            new HlaProperties(
//                List.of(
//                    new HlaProfile(
//                        "hla",
//                        ModuleLanguage.KOTLIN,
//                        "../lib",
//                        "src/main/kotlin/pl/bratek20/hla",
//                        "src/testFixtures/kotlin/pl/bratek20/hla",
//                        Collections.emptyList(),
//                        false
//                    ),
//                    new HlaProfile(
//                        "PlayFab",
//                        ModuleLanguage.TYPE_SCRIPT,
//                        "../lib",
//                        "src/main/kotlin/pl/bratek20/hla",
//                        "src/testFixtures/kotlin/pl/bratek20/hla",
//                        Collections.emptyList(),
//                        false
//                    ),
//                    new HlaProfile(
//                        "WohProperties",
//                        ModuleLanguage.KOTLIN,
//                        "../woh-properties",
//                        "src/main/java/com/rortos/woh",
//                        "src/testFixtures/java/com/rortos/woh",
//                        List.of(
//                            "NamedTypes",
//                            "CustomTypes",
//                            "CustomTypesMapper",
//                            "Properties",
//                            "Builders"
//                        ),
//                        false
//                    )
//                )
//            )
//        );

        var facade = context.get(HlaFacade.class);

        var operationName = args[0];
        switch (operationName) {
            case "start":
                facade.startModule(parseModuleOperationArgs(args));
                break;
            case "update":
                facade.updateModule(parseModuleOperationArgs(args));
                break;
            case "updateAll":
                facade.updateAllModules(parseAllModulesOperationArgs(args));
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operationName);
        }
    }

    private static ModuleOperationArgs parseModuleOperationArgs(String[] args) {
        return new ModuleOperationArgs(
            new Path(args[1]),
            new ProfileName(args[2]),
            new ModuleName(args[3])
        );
    }

    private static AllModulesOperationArgs parseAllModulesOperationArgs(String[] args) {
        return new AllModulesOperationArgs(
            new Path(args[1]),
            new ProfileName(args[2])
        );
    }
}
