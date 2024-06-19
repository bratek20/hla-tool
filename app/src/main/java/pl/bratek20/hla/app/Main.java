package pl.bratek20.hla.app;

import com.github.bratek20.architecture.context.spring.SpringContextBuilder;
import com.github.bratek20.logs.context.Slf4jLogsImpl;
import pl.bratek20.hla.directory.api.Path;
import pl.bratek20.hla.directory.context.DirectoryImpl;
import pl.bratek20.hla.facade.api.*;
import pl.bratek20.hla.facade.context.FacadeImpl;

public class Main {
    public static void main(String[] args) {
        var context = new SpringContextBuilder()
            .withModules(
                new Slf4jLogsImpl(),

                new DirectoryImpl(),
                new FacadeImpl()
            )
            .build();

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
        return ModuleOperationArgs.Companion.create(
            new Path(args[1]),
            new ProfileName(args[2]),
            new ModuleName(args[3])
        );
    }

    private static AllModulesOperationArgs parseAllModulesOperationArgs(String[] args) {
        return AllModulesOperationArgs.Companion.create(
            new Path(args[1]),
            new ProfileName(args[2])
        );
    }
}
