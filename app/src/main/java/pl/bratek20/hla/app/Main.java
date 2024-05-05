package pl.bratek20.hla.app;

import pl.bratek20.architecture.context.spring.SpringContextBuilder;
import pl.bratek20.hla.directory.api.Path;
import pl.bratek20.hla.directory.impl.DirectoriesLogic;
import pl.bratek20.hla.directory.impl.DirectoryContextModule;
import pl.bratek20.hla.facade.api.GeneratedModuleArgs;
import pl.bratek20.hla.facade.api.HlaFacade;
import pl.bratek20.hla.facade.impl.FacadeContextModule;
import pl.bratek20.hla.facade.impl.HlaFacadeImpl;
import pl.bratek20.hla.generation.api.ModuleLanguage;
import pl.bratek20.hla.generation.api.ModuleName;

public class Main {
    public static void main(String[] args) {
        var context = new SpringContextBuilder()
            .withModule(new FacadeContextModule())
            .withModule(new DirectoryContextModule())
            .build();

        var directories = context.get(DirectoriesLogic.class);
        var facade = context.get(HlaFacade.class);

        var moduleName = new ModuleName(args[0]);
        var language = ModuleLanguage.valueOf(args[1]);

        var outPath = new Path("../tmp");
        directories.deleteDirectory(outPath);
        facade.generateModule(
            new GeneratedModuleArgs(
                moduleName,
                language,
                new Path("src/main/resources/hla"),
                outPath
            )
        );
    }
}
