package pl.bratek20.hla.app;

import pl.bratek20.hla.directory.api.Path;
import pl.bratek20.hla.directory.impl.DirectoriesLogic;
import pl.bratek20.hla.facade.api.GeneratedModuleArgs;
import pl.bratek20.hla.facade.impl.HlaFacadeImpl;
import pl.bratek20.hla.generation.api.ModuleLanguage;
import pl.bratek20.hla.generation.api.ModuleName;

public class Main {
    public static void main(String[] args) {
        var directories = new DirectoriesLogic();
        var facade = new HlaFacadeImpl(directories);

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
