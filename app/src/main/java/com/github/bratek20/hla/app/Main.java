package com.github.bratek20.hla.app;

import com.github.bratek20.architecture.context.spring.SpringContextBuilder;
import com.github.bratek20.logs.context.Slf4jLogsImpl;
import com.github.bratek20.utils.directory.api.Path;
import com.github.bratek20.utils.directory.context.DirectoryImpl;
import com.github.bratek20.hla.facade.api.*;
import com.github.bratek20.hla.facade.context.FacadeImpl;

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
            case "startAll":
                facade.startAllModules(parseAllModulesOperationArgs(args));
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
